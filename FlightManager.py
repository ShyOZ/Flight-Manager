import subprocess
import datetime
import pathlib

from flask import Flask, request


def run():
    app = Flask(__name__)

    @app.route("/")
    def hello():
        return "hello!"

    def flights(direction):
        arguments = [f"direction-{direction}"]
        if len(request.args) > 0:
            options = ["outformat","airline","city","terminal","country","airport"]
            days = ["sunday","monday","tuesday","wednesday","thursday","friday","saturday"]
            day_of_week = ""
            for day in days:
                answer = request.args.get(day)
                if answer is True:
                    if day_of_week == "":
                        day_of_week = "day_of_week-" + day
                    else:
                        day_of_week += "," + day
            if day_of_week != "":
                arguments += [day_of_week]
                    
            try:
                date1_str = f'{request.args.get("year1")} {request.args.get("month1")} {request.args.get("day1")} 00:00'
                date1_str = (datetime.datetime.strptime(date1_str, "%Y %m %d %H:%M")).strftime("%Y/%m/%d %H:%M")
                arguments+=["from-"+date1_str]
            except:
                ...
            try:
                date2_str = f'{request.args.get("year2")} {request.args.get("month2")} {request.args.get("day2")} 00:00'
                date2_str = (datetime.datetime.strptime(date2_str, "%Y %m %d %H:%M")).strftime("%Y/%m/%d %H:%M")
                arguments+=["to-"+date2_str]
            except:
                ...
            for option in options:
                answer = request.args.get(option)
                if answer is not None:
                    arguments += [option + "-" + answer]
                    
        path_to_folder = str(pathlib.Path(__file__).parent.absolute())
        return subprocess.check_output(["java", "-cp", "bin",
        "core.FlightManager"] + arguments)
       

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
    subprocess.run(["javac","-d","bin","src/core/*.java"])
    run()
