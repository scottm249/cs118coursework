import org.junit.Test;
import static org.junit.Assert.*;
import uk.ac.warwick.dcs.maze.logic.Maze;
import uk.ac.warwick.dcs.maze.generators.PrimGenerator;
import uk.ac.warwick.dcs.maze.logic.RobotImpl;

/*
  This class contains unit tests for the RandomController class.
*/
public class RandomControllerTest {
  /*
    Tests whether the random controller causes the robot
    to walk into walls.
  */
  @Test(timeout=10000)
  public void doesNotRunIntoWallsTest() {
    // Generate a random maze
    Maze maze = (new PrimGenerator()).generateMaze();

    // Initialise the robot
    RobotImpl robot = new RobotImpl();
    robot.setMaze(maze);

    // Initialise the random robot controller
    RandomController controller = new RandomController();
    controller.setRobot(robot);

    // Run the controller
    controller.start();

    // Test whether the robot walked into walls during this run
    assertTrue(
      "RandomController walks into walls!",
      robot.getCollisions() == 0);
  }

  /*
    Tests whether the random controller causes the robot to find
    the end of three randomly generated mazes.
  */
  @Test(timeout=20000)
  public void reachEndTest() {
    // Reapeat test with three different mazes
    for (int i = 0; i < 3; i++) {
      // Generate a random maze
      Maze maze = (new PrimGenerator()).generateMaze();

      // Initialise the robot
      RobotImpl robot = new RobotImpl();
      robot.setMaze(maze);

      // Initialise the random robot controller
      RandomController controller = new RandomController();
      controller.setRobot(robot);

      // Run the controller
      controller.start();

      // Test whether the robot reached the target
      assertTrue(
        "RandomController doesn't finish",
        robot.getLocation().equals(robot.getTargetLocation()));
    }
  }
}
