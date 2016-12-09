import pprint
import string

from gurobipy.gurobipy import tuplelist, Model, GRB, quicksum, LinExpr
from openpyxl import load_workbook


# Class to run our entire problem
class PaperAssignement(object):
    def __init__(self, input_file, n_papers, n_referees):
        self.input_file = input_file
        # number of papers and reviewers
        self.n_papers = n_papers
        self.n_referees = n_referees

        # store a dictionary (paper, reviewer) -> cost
        # this can be seen as a cost matrix and will be used later for producing our decision variables x[i,j]
        self.preferences = self.__read_input()

        # Categorial list of referees -> (Referee1, Referee2 ...)
        self.referees = set([referee for (paper, referee) in self.preferences])
        # Categorial list of papers -> (Paper1, Paper2 ...)
        self.papers = set([paper for (paper, referee) in self.preferences])
        # all possible pairs of assignments between referees and papers
        # this are going to be our decision variables x[i,j]
        self.assignments = tuplelist([t for t in self.preferences])

        # Where we are going to store our model.
        self.model = None

    # Maps a preference to a cost.
    # Costs are higher as preference decreases
    # We are
    def __map_preferences(self, preference):
        return {
            'yes': 2,
            'maybe': 4,
            'no': 6,
            'conflict': 8

        }[preference]

    # We read straight from the Excel sheet and create a dictionary
    # (paper, referee) -> cost
    def __read_input(self):
        preferences = {}
        wb = load_workbook(self.input_file)
        # Get the corresponding sheet.
        sheet = wb['data']
        # Little trick to transform numbers into upper case letters
        # for accessing cells. We can do this here because referees
        # only go to `V`. Otherwise this would scramble results.
        d = dict(enumerate(string.ascii_uppercase, 1))

        # Read the matrix and map to costs, using __map_preferences.
        for paper_idx in range(1, self.n_papers + 1):
            for referee_idx in range(1, self.n_referees + 1):
                position = d[referee_idx + 1] + str(paper_idx + 1)
                paper_name = str(sheet['A' + str(paper_idx + 1)].value)
                reviewer_name = str(sheet[str(d[referee_idx + 1]) + '1'].value)
                v = self.__map_preferences(str(sheet[position].value))
                preferences[(paper_name, reviewer_name)] = v
        return preferences

    # We build our model with the objective function and the constraints.
    def build_model(self):
        self.model = Model("PaperAssignement")

        # We want integer variables with an upper bound of 1. Each assignment (paper, referee) can be done only once.
        x = self.model.addVars(self.assignments, vtype=GRB.INTEGER, ub=1.0, name="x")

        # Since our preferences are seen in terms of cost, every assignment will pay a penalty of x[i,j] * cost.
        # If we make a match, x[i,j] = 1 and the cost will be paid. We are trying to minimize the overall cost.
        self.model.setObjective(
            quicksum(self.preferences[(paper, referee)] * x[paper, referee] for paper, referee in self.assignments),
            GRB.MINIMIZE
        )

        # We want every paper to be reviewed by at least 3 people. That means that constraint p will
        # sum(x[p, referee_j]) for paper p over all referees and that number has to be at least 3
        # (refer to writeup for equation).
        # Gurobi provides us a simple way to add all of these constraints at once using list comprehension
        self.model.addConstrs((x.sum(paper, '*') >= 3 for paper in self.papers), "c1")

        # Our next to constraints will be put in place to ensure an almost-even number of papers assigned to every
        # referee. We would like the number of papers assigned to every referee to stay within a margin of the mean of
        # assignments made.
        # We construct the expression to find that mean.
        avg = LinExpr() + (x.sum('*', '*') / len(self.referees))

        # The number of assignments per referee will be in the range [avg - 1, avg + 1]
        # We want number of assigned papers to a referee to be at most 1 below the mean of assignments.
        self.model.addConstrs((x.sum('*', reviewer) >= avg - 1 for reviewer in self.referees), "c2")
        # We want number of assigned papers to a referee to be at most 1 above the mean of assignments.
        self.model.addConstrs((x.sum('*', reviewer) <= avg + 1 for reviewer in self.referees), "c3")

        self.model.update()

        self.model.write(filename="Model_PaperAssignement.lp")

    def run(self):
        # If our model is None, we need to build it first
        if self.model is None:
            print 'Please build the model first.'
            return
        self.model.optimize()
        status = self.model.status
        # Check if we ran into an unbounded model.
        if status == GRB.Status.UNBOUNDED:
            print('The model cannot be solved because it is unbounded')
            exit(0)

        # If our model is optimal, we write to file all relevant information.
        if status == GRB.Status.OPTIMAL:
            target = open('results.txt', 'w')
            pp = pprint.PrettyPrinter(indent=4)
            # Obtain the decision variables
            dec_vars = self.model.getVars()
            # Prepare to count the number of papers per referee.
            referees_count = {r: 0 for r in self.referees}
            # Prepare to display papers per referee.
            referees_assignment = {r: [] for r in self.referees}
            # Prepare to count the number reviews per paper.
            papers_count = {p: 0 for p in self.papers}

            target.write('The optimal objective is  %g\n\n' % self.model.objVal)

            print('\n\n')
            print('The optimal objective is %g\n' % self.model.objVal)
            target.write('\n----------------------------------\n')
            target.write('Decision variables values:\n')

            for var in dec_vars:
                # Display the assignment
                target.write('%s = %s\n' % (var.varName, (str(int(var.x)))))
                var_name = var.varName[2:-1]
                # Just parsing of the name to obtain a meaningful dictionary key.
                referees_count[var_name.split(',')[1]] += var.x
                papers_count[var_name.split(',')[0]] += var.x
                referees_assignment[var_name.split(',')[1]].append(var_name.split(',')[0])

            target.write('\n--------------------------------------------------\n')
            target.write('Number of papers assigned per referee\n')

            for k in referees_count:
                target.write('%s: %s\n' % (k, referees_count[k]))

            target.write('\n--------------------------------------------------\n')
            target.write('Number of reviews per paper\n')

            for k in papers_count:
                target.write('%s: %s\n' % (k, papers_count[k]))

            target.write('\n--------------------------------------------------\n')
            avg_paper = sum([papers_count[k] for k in papers_count]) / len(papers_count)
            target.write('Average number of reviews per paper: %s\n' % avg_paper)
            print('Average number of reviews per paper: %s' % avg_paper)

            target.write('--------------------------------------------------')
            avg_referee = sum([referees_count[k] for k in referees_count]) / len(referees_count)
            target.write('\nAverage number of papers per referee: %s' % avg_referee)
            print('\nAverage number of papers per referee: %s' % avg_referee)
            target.write('\n--------------------------------------------------\n')
            print("""\n\nFile 'results.txt' generated. Please consult it for details\n""")

            exit(0)

        if status != GRB.Status.INF_OR_UNBD and status != GRB.Status.INFEASIBLE:
            print('Optimization was stopped with status %d' % status)
            exit(0)  # def extract_preferences(filename='paper_pref.xlsx', npapers=71, nreviewers=21):


if __name__ == "__main__":
    pa = PaperAssignement(input_file='paper_pref.xlsx', n_papers=71, n_referees=21)
    pa.build_model()
    pa.run()
