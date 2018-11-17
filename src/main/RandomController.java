import uk.ac.warwick.dcs.maze.logic.*;
import java.awt.Point;

/*
  This controller uses the generation of random numbers to control the robots
  movements. It is because of this random behaviour that the robot will always
  find the end of the maze providing it is not blocked off by walls. The
  downside to this controller is it can take a long time to reach the end.
*/

public class RandomController implements IRobotController {
  // The robot in the maze
  private IRobot robot;
  // A flag to indicate whether we are looking for a path
  private boolean active = false;
  // A value (in ms) indicating how long we should wait
  // between moves
  private int delay;
  // The direction the robot is facing
  private int direction;

  // This method is called when the "start" button is clicked
  // in the user interface
  public void start() {
    // Set flag to start looking for a path
    this.active = true;
    // Loop while we haven't found the exit and the agent
    // has not been interrupted
    while(!robot.getLocation().equals(robot.getTargetLocation()) && active) {
      // Move in a random direction on average every one in eight moves or
      // if there is a wall in front of the robot otherwise carry on fowards
      if (robot.look(IRobot.AHEAD) == IRobot.WALL || Math.random() < 0.125) {
        // Move in a random direction and log the movement
        randMove();
      } else {
        // Move foward one step and log the movement
        robot.advance();
        robot.getLogger().log(IRobot.AHEAD);
      }

      // wait for a while if we are supposed to
      if (delay > 0) {
        robot.sleep(delay);
      }
    }
  }

  // This method moves the robot in a random direction and logs the movement
  public void randMove() {
    // Start of loop
    do {
      // Using the Math.round method caused the old generator to be
      // biased towards picking a 1 or 2 because of the following ranges
      // [0 - 0.49] = 0
      // [0.5 - 1.49] = 1
      // [1.5 - 2.49] = 2
      // [2.5 - 3] = 3

      // Generate a random number in the range 0 <= n < 4 and round it down
      // producing an integer between 0-3 (inclusive) with equal probability
      int rand = (int)Math.floor(Math.random()*4);

      // Set direction variable to direction, as determined
      // by the random number that was generated:
      // 0: foward
      // 1: left
      // 2: right
      // 3: behind
      switch (rand) {
        case 0:
          // Set the direction variable to ahead
          direction = IRobot.AHEAD;
          break;
        case 1:
          // Set the direction variable to left
          direction = IRobot.LEFT;
          break;
        case 2:
          // Set the direction variable to right
          direction = IRobot.RIGHT;
          break;
        case 3:
          // Set the direction variable to behind
          direction = IRobot.BEHIND;
          break;
      }

    // Loop back to do statement to generate a new direction if the robot is
    // facing a wall otherwise end loop
    } while (robot.look(direction) == IRobot.WALL);

    // Make the robot face in the direction generated then log
    // this direction as a movement
    robot.face(direction);
    robot.getLogger().log(direction);
    // Move one step in the direction the robot is facing
    robot.advance();
  }

  // this method returns a description of this controller
  public String getDescription() {
    return "A controller which randomly chooses where to go";
  }

  // sets the delay
  public void setDelay(int millis) {
    delay = millis;
  }

  // gets the current delay
  public int getDelay() {
    return delay;
  }

  // stops the controller
  public void reset() {
    active = false;
  }

  // sets the reference to the robot
  public void setRobot(IRobot robot) {
    this.robot = robot;
  }
}
