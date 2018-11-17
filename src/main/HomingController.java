import uk.ac.warwick.dcs.maze.logic.*;
import java.awt.Point;

/*
  This controller uses absolute directions of the target relative to the robot
  this allows the robot to head in the direction of the target therefore homing
  in on its target. When run on most mazes generated by the loopyGenerator this
  controller is much faster however when run on a maze generated by the
  primGenerator most of the time it will not actually reach the target due to
  the behaviour requested in the specification, this behaviour is documented
  in the testing of this controller.
*/

public class HomingController implements IRobotController {
  // The robot in the maze
  private IRobot robot;
  // A flag to indicate whether we are looking for a path
  private boolean active = false;
  // A value (in ms) indicating how long we should wait
  // between moves
  private int delay;

  // This method is called when the "start" button is clicked
  // in the user interface
  public void start() {
    // Set flag to start looking for a path
    this.active = true;
    // Loop while we haven't found the exit and the agent
    // has not been interrupted
    while(!robot.getLocation().equals(robot.getTargetLocation()) && active) {
      // Set the heading of the of the robot in the direction
      // of the target without pointing towards a wall
      robot.setHeading(determineHeading());
      // Face in the direction of the new heading
      robot.face(IRobot.AHEAD);
      // Move one step foward
      robot.advance();

      // Wait for a while if we are supposed to
      if (delay > 0) {
        robot.sleep(delay);
      }
    }
  }

  // This method returns 1 if the target is north of the
  // robot, -1 if the target is south of the robot, or
  // 0 otherwise.
  public byte isTargetNorth() {
    // If the robots y coordinate is below the tagets return -1
    if (robot.getLocation().y < robot.getTargetLocation().y) {
      return -1;
    // If the robots y coordinate is above the tagets return 1
    } else if (robot.getLocation().y > robot.getTargetLocation().y) {
      return 1;
    // If the target and robot have equal y coordinates then return 0
    } else {
      return 0;
    }
  }

  // This method returns 1 if the target is east of the
  // robot, -1 if the target is west of the robot, or
  // 0 if otherwise.
  public byte isTargetEast() {
    // If the robots x coordinate is below the tagets return 1
    if (robot.getLocation().x < robot.getTargetLocation().x) {
      return 1;
    // If the robots x coordinate is above the tagets return -1
    } else if (robot.getLocation().x > robot.getTargetLocation().x) {
      return -1;
    // If the target and robot have equal x coordinates then return 0
    } else {
      return 0;
    }
  }

  // This method causes the robot to look to the absolute
  // direction that is specified as argument and returns
  // the type of square that is there.
  public int lookHeading(int absoluteDirection) {
    // Set the robot heading to the argument
    robot.setHeading(absoluteDirection);
    // Return the type of square ahead of the robot
    return robot.look(IRobot.AHEAD);
  }

  // This method determines the heading in which the robot
  // should head to move closer to the target without hitting
  // a wall
  public int determineHeading() {
    // Set variables to represent the status of the squares
    // in the target directions respectively
    int n = verticalStatus();
    int e = horizontalStatus();
    // Select a random heading from a choice of two headings
    int h = chooseHead(n, e);

    // If the vertical path is blocked or the target is vertically level
    // but horizontal is not then return the relevant heading east or west
    if (n == 2 && e == 3) {
      return IRobot.EAST;
    } else if (n == 2 && e == 4) {
      return IRobot.WEST;
    // If the horizontal path is blocked or the target is horizontally level
    // but vertical is not then return the relevant heading north or south
    } else if (e == 2 && n == 0) {
      return IRobot.NORTH;
    } else if (e==2 && n == 1) {
      return IRobot.SOUTH;
    // If both paths towards target are blocked then set a random heading
    } else if (n == 2 && e == 2) {
      return randHead();
    // If there are two possible paths towards the target that are not blocked
    // then select at random between the two paths
    } else {
      if (h == 0) {
        return IRobot.NORTH;
      } else if (h == 1) {
        return IRobot.SOUTH;
      } else if (h == 3) {
        return IRobot.EAST;
      } else if (h == 4) {
        return IRobot.WEST;
      }
    }
    // If there has been an error return 0
    return 0;
  }

  // This method first works out whether the target is north, south or
  // level then it will return either a 2 if the direction is blocked by a
  // wall or the target is level with the robot, a 0 if the robot should
  // head north or a 1 if the robot should head south
  public int verticalStatus() {
    // Work out whether target is north, south or level
    switch (isTargetNorth()) {
      case 1:
        // Check for a wall to the north and return 2 if there is a wall
        // or 0 otherwise
        if (lookHeading(IRobot.NORTH) == IRobot.WALL) {
          return 2;
        } else {
          return 0;
        }
      case -1:
        // Check for a wall to the south and return 2 if there is a wall
        // or 1 otherwise
        if (lookHeading(IRobot.SOUTH) == IRobot.WALL) {
          return 2;
        } else {
          return 1;
        }
      case 0:
        // Return 2 when the target is level
        return 2;
    }
    // If there has been an error return -1
    return -1;
  }

  // This method first works out whether the target is east, west or
  // level then it will return either a 2 if the direction is blocked
  // by a wall or the target is level with the robot, a 3 if the robot
  // should head east or a 4 if the robot should head west
  public int horizontalStatus() {
    // Work out whether the target is north, south or level
    switch (isTargetEast()) {
      case 1:
        // Check for a wall to the east and return 2 if there is a wall
        // or 3 otherwise
        if (lookHeading(IRobot.EAST) == IRobot.WALL) {
          return 2;
        } else {
          return 3;
        }
      case -1:
        // Check for a wall to the west and return 2 if there is a wall
        // or 4 otherwise
        if (lookHeading(IRobot.WEST) == IRobot.WALL) {
          return 2;
        } else {
          return 4;
        }
      case 0:
        // Return 2 when the target is level
        return 2;
    }
    // If there has been an error return -1
    return -1;
  }

  // This method returns a randomly selected argument with equal probability
  public int chooseHead(int a, int b) {
    if (Math.random() < 0.5) {
      return a;
    } else {
      return b;
    }
  }

  // This method returns a random heading that doesn't
  // point towards a wall
  public int randHead() {
    // The heading of the robot
    int head = 0;
    // Start of loop
    do {
      // Generate a random number between 0-3 (inclusive)
      int rand = (int)Math.floor(Math.random()*4);

      // Set head variable to random heading based on random number
      switch (rand) {
        case 0:
          head = IRobot.NORTH;
          break;
        case 1:
          head = IRobot.EAST;
          break;
        case 2:
          head = IRobot.WEST;
          break;
        case 3:
          head = IRobot.SOUTH;
          break;
      }

    // Loop back to do statement to generate a new heading if the robot is
    // facing a wall otherwise end loop
    } while (lookHeading(head) == IRobot.WALL);

    // Return heading
    return head;
  }

  // this method returns a description of this controller
  public String getDescription() {
    return "A controller which homes in on the target";
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
