import subprocess
import datetime


from flask import Flask, request


def run():
    app = Flask(__name__)

    @app.route("/")
    def hello():
        return "hello!"

    def flights(direction):
        arguments = [f"direction-{direction}"]
        if len(request.args) > 0:
            options = ["airline", "city", "terminal", "country", "airport","day_of_week"]
            fromdate = ["day1", "month1", "year1"]
            todate = ["day2", "month2", "year2"]
            
            for option in options:
                answer = request.args.get(option)
                if answer is not None:
                    arguments += [option + "-" + answer]

        return subprocess.check_output(["java", "-cp", "bin", "core.FlightManager"] + arguments)

    @app.route("/departures")
    def departures():
        return flights("departures")

    @app.route("/arrivals")
    def arrivals():
        return flights("arrivals")

    app.run(host="0.0.0.0", port=8000)


if __name__ == "__main__":
    run()
