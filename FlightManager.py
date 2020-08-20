import subprocess
import datetime

from flask import Flask, request, render_template


def run():
    app = Flask(__name__)

    @app.route("/")
    def hello():
        return "hello!"

    @app.route("/flightForm", methods=["POST", "GET"])
    def get_info_from_html():
        if request.method == "POST":
            print(["direction-"+request.form["direction"], "outformat-html"] + parse_dictionary(request.form))
            return ["direction-"+request.form["direction"], "outformat-html"] + parse_dictionary(request.form)
        else:
            return render_template("FlightForm.html")

    def parse_dictionary(dictionary):
        arguments = []
        if len(dictionary) > 0:
            options = ["outformat", "airline", "city", "terminal", "country", "airport"]
            days = ["sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"]
            day_of_week = ""
            for day in days:
                if (dictionary.get(day) is not None) and (dictionary.get(day) != ""):
                    if day_of_week == "":
                        day_of_week = "day_of_week-" + day
                    else:
                        day_of_week += "," + day
            if day_of_week != "":
                arguments += [day_of_week]

            # the following try/except blocks refer to request.args i.e. when reading from address field
            try:
                date1_str = f'{dictionary.get("year1")} {dictionary.get("month1")} {dictionary.get("day1")} 00:00'
                date1_str = (datetime.datetime.strptime(date1_str, "%Y %m %d %H:%M")).strftime("%Y/%m/%d %H:%M")
                arguments += ["from-" + date1_str]
            except:
                ...
            try:
                date2_str = f'{dictionary.get("year2")} {dictionary.get("month2")} {dictionary.get("day2")} 00:00'
                date2_str = (datetime.datetime.strptime(date2_str, "%Y %m %d %H:%M")).strftime("%Y/%m/%d %H:%M")
                arguments += ["to-" + date2_str]
            except:
                ...
                # the following try/except block refers to request.form i.e. when reading from a HTML form
            try:
                date1_str = f'{dictionary.get("startdate")}'
                if date1_str != "":
                    arguments += ["from-" + date1_str + ' 00:00']
            except:
                ...
            try:
                date2_str = f'{dictionary.get("enddate")}'
                if date2_str != "":
                    arguments += ["to-" + date2_str + ' 00:00']
            except:
                ...

            for option in options:
                answer = dictionary.get(option)
                if (answer is not None) and (answer != ""):
                    arguments += [option + "-" + answer]

            return arguments

    def flights(direction):
        arglist = parse_dictionary(request.args)
        arguments = [f"direction-{direction}"]
        if arglist is not None:
            arguments += arglist
        return show_java_program_by_args(arguments)

    def show_java_program_by_args(arguments):

        return subprocess.check_output(["java", "-cp", "bin", "core.FlightManager"] + arguments)

    @app.route("/departures")
    def departures():
        return flights("departures")

    @app.route("/arrivals")
    def arrivals():
        return flights("arrivals")

    @app.route("/all")
    def all():
        return flights("all")

    app.run(host="0.0.0.0", port=8000)


if __name__ == "__main__":
    subprocess.run(["javac", "-d", "bin", "src/core/*.java"])
    run()
