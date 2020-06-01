import subprocess

import FlightManager

if __name__ == "__main__":
    subprocess.run(["python", "init.py"])
    FlightManager.FlightManager.run()
