import subprocess

from flask import Flask, request


class FlightManager:
    @staticmethod
    def run():
        app = Flask(__name__)

        @app.route("/")
        def hello():
            return "hello!"

        @app.route("/flight-manager")
        def flight_manager():
            if len(request.args) > 0:
                arguments = []
                options = ["after", "before", "airline", "city", "terminal", "direction"]
                for option in options:
                    answer = request.args.get(option)
                    if answer is not None:
                        arguments += [option + "-" + answer]

                return subprocess.check_output(["java", "-cp", "bin", "core.FlightManager"] + arguments)
            else:
                return subprocess.check_output(["java", "-cp", "bin", "core.FlightManager", "all"])

        app.run()
