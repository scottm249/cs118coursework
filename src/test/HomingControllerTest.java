import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import uk.ac.warwick.dcs.maze.logic.Maze;
import uk.ac.warwick.dcs.maze.generators.PrimGenerator;
import uk.ac.warwick.dcs.maze.logic.IRobot;
import uk.ac.warwick.dcs.maze.logic.RobotImpl;
import java.awt.Point;

/*
  This class contains unit tests for the HomingController class.
*/
public class HomingControllerTest {
  // The dimensions of the test maze
  private int columns = 5;
  private int rows = 5;
  // The maze used for testing
  private Maze maze;
  // The robot used for testing
  private RobotImpl robot;
  // The controller used for testing
  private HomingController controller;
  // The number of steps the robot has taken
  private long steps;

  /*
    This method is run before all tests.
  */
  @Before
  public void setupTests() {
    // Generate a maze with the test dimensions
    this.maze = new Maze(this.columns, this.rows);

    // Fill the maze with passages
    for (int i=0; i<this.columns; i++) {
      for (int j=0; j<this.rows; j++) {
        this.maze.setCellType(i, j, Maze.PASSAGE);
      }
    }

    // Set the starting point somewhere near the middle
    this.maze.setStart(2,2);
    this.maze.setFinish(0,0);

    // Initialise the robot
    this.robot = new RobotImpl();
    this.robot.setMaze(this.maze);

    // Initialise the random robot controller
    this.controller = new HomingController();
    this.controller.setRobot(this.robot);
  }

  /*
    Tests whether the robot homes in on the target without
    making any unnecessary moves in a blank maze
  */
  @Test(timeout=10000)
  public void blankMazeHomingBehaviourTest() {
    // Move the target two cells east of the robot and
    // run the controller and see whether the number of steps
    // taken is equal to 2
    this.robot.setTargetLocation(new Point(4, 2));
    this.controller.start();
    steps = this.robot.getSteps();

    assertTrue(
      "Robot doesn't home in on target to the east",
      steps == 2);

    // Move the target to two cells south of the robot and
    // run the controller and see whether the number of steps
    // taken is equal to 2
    this.robot.setTargetLocation(new Point(4, 4));
    this.controller.start();
    steps = this.robot.getSteps() - 2;

    assertTrue(
      "Robot doesn't home in on target to the south",
      steps == 2);

    // Move the target to four cells west and four cells north
    // of the robot and run the controller and see whether the
    // number of steps taken is equal to 8
    this.robot.setTargetLocation(new Point(0, 0));
    this.controller.start();
    steps = this.robot.getSteps() - 4;

    assertTrue(
      "Robot doesn't home in on target to the north west",
      steps == 8);
  }

  /*
    Tests whether the robot homes in on the target without
    making any unnecessary moves in a maze with one wall.
  */
  @Test(timeout=10000)
  public void collisionHomingBehaviourTest() {
    // Make the cell that is north east from the robot into a wall
    // then move the target to be opposite the robot on the other
    // side of the wall and see whether the number of steps is
    // equal to 4
    this.maze.setCellType(3, 1, Maze.WALL);
    this.robot.setTargetLocation(new Point(4, 0));
    this.controller.start();
    assertTrue(
      "Robot doesn't home around the wall correctly",
      this.robot.getSteps() == 4);
  }

  /*
    This test is for behaviour not required by the specification as
    the homing behaviour that the random controller is programmed with
    will cause the robot to enter an infinite loop in this situation. This
    test will check the robot behaviour when the only path to get to the
    target involves chosing a path repeatedly that moves the robot away from
    the target.
  */
  // @Test(timeout=10000)
  // public void largeWallNavigationTest() {
  //   // Change all cells but the bottom cell where the x coordinate is 3
  //   // to be a wall then move the target to the other side of the wall and
  //   // test to see if the robot can get to the other side of the wall
  //   // in a reasonable number of steps
  //   for (int i = 0; i < 4; i++) {
  //     this.maze.setCellType(3, i, Maze.WALL);
  //   }
  //   this.robot.setTargetLocation(new Point(4, 1));
  //   while (this.robot.getSteps() < 500) {
  //     this.robot.setHeading(this.controller.determineHeading());
  //     this.robot.face(IRobot.AHEAD);
  //     this.robot.advance();
  //   }
  //   assertTrue(
  //     "The robot cannot get past a continued wall (not required by spec)",
  //     this.robot.getLocation().equals(this.robot.getTargetLocation()));
  // }

  /*
    Tests whether the homing controller's isTargetNorth
    method works as specified.
  */
  @Test(timeout=10000)
  public void isTargetNorthTest() {
    // Move the target to some cells north of the robot and
    // test whether isTargetNorth correctly identifies this
    for(int i=0; i<this.columns; i++) {
      this.robot.setTargetLocation(new Point(i,0));

      assertTrue(
        "HomingController doesn't think the target is north!",
        this.controller.isTargetNorth() == 1);
    }

    // Move the target to some cells south of the robot and
    // test whether isTargetNorth correctly identifies this
    for(int i=0; i<this.columns; i++) {
      this.robot.setTargetLocation(new Point(i,4));

      assertTrue(
        "HomingController doesn't think the target is south!",
        this.controller.isTargetNorth() == -1);
    }

    // Move the target to some cells on the same y-level as the
    // robot and test whether isTargetNorth correctly identifies this
    for(int i=0; i<this.columns; i++) {
      this.robot.setTargetLocation(new Point(i,2));

      assertTrue(
        "HomingController doesn't think the target is on the same level!",
        this.controller.isTargetNorth() == 0);
    }
  }

  /*
    Tests whether the homing controller's isTargetEast
    method works as specified.
  */
  @Test(timeout=10000)
  public void isTargetEastTest() {
    // Move the target to some cells east of the robot and
    // test whether isTargetEast correctly identifies this
    for(int i=0; i<this.columns; i++) {
      this.robot.setTargetLocation(new Point(4,i));

      assertTrue(
        "HomingController doesn't think the target is east!",
        this.controller.isTargetEast() == 1);
    }

    // Move the target to some cells west of the robot and
    // test whether isTargetEast correctly identifies this
    for(int i=0; i<this.columns; i++) {
      this.robot.setTargetLocation(new Point(0,i));

      assertTrue(
        "HomingController doesn't think the target is west!",
        this.controller.isTargetEast() == -1);
    }

    // Move the target to some cells on the same x-level as the
    // robot and test whether isTargetEast correctly identifies this
    for(int i=0; i<this.columns; i++) {
      this.robot.setTargetLocation(new Point(2,i));

      assertTrue(
        "HomingController doesn't think the target is on the same level!",
        this.controller.isTargetEast() == 0);
    }
  }

  /*
    Tests whether the homing controller's lookHeading method
    works correctly.
  */
  @Test(timeout=10000)
  public void lookHeadingTest() {
    // Add some walls to the maze
    this.maze.setCellType(2, 1, Maze.WALL);
    this.maze.setCellType(2, 3, Maze.WALL);

    // Test lookHeading for when the robot is facing north
    this.robot.setHeading(IRobot.NORTH);
    assertTrue(
      "HomingController doesn't see a wall in the north!",
      this.controller.lookHeading(IRobot.NORTH) == IRobot.WALL);
    assertTrue(
      "HomingController doesn't see a passage in the east!",
      this.controller.lookHeading(IRobot.EAST) == IRobot.PASSAGE);
    assertTrue(
      "HomingController doesn't see a wall in the south!",
      this.controller.lookHeading(IRobot.SOUTH) == IRobot.WALL);
    assertTrue(
      "HomingController doesn't see a passage in the west!",
      this.controller.lookHeading(IRobot.WEST) == IRobot.PASSAGE);

    // Test lookHeading for when the robot is facing east
    this.robot.setHeading(IRobot.EAST);
    assertTrue(
      "HomingController doesn't see a wall in the north!",
      this.controller.lookHeading(IRobot.NORTH) == IRobot.WALL);
    assertTrue(
      "HomingController doesn't see a passage in the east!",
      this.controller.lookHeading(IRobot.EAST) == IRobot.PASSAGE);
    assertTrue(
      "HomingController doesn't see a wall in the south!",
      this.controller.lookHeading(IRobot.SOUTH) == IRobot.WALL);
    assertTrue(
      "HomingController doesn't see a passage in the west!",
      this.controller.lookHeading(IRobot.WEST) == IRobot.PASSAGE);

    // Test lookHeading for when the robot is facing south
    this.robot.setHeading(IRobot.SOUTH);
    assertTrue(
      "HomingController doesn't see a wall in the north!",
      this.controller.lookHeading(IRobot.NORTH) == IRobot.WALL);
    assertTrue(
      "HomingController doesn't see a passage in the east!",
      this.controller.lookHeading(IRobot.EAST) == IRobot.PASSAGE);
    assertTrue(
      "HomingController doesn't see a wall in the south!",
      this.controller.lookHeading(IRobot.SOUTH) == IRobot.WALL);
    assertTrue(
      "HomingController doesn't see a passage in the west!",
      this.controller.lookHeading(IRobot.WEST) == IRobot.PASSAGE);

    // Test lookHeading for when the robot is facing west
    this.robot.setHeading(IRobot.WEST);
    assertTrue(
      "HomingController doesn't see a wall in the north!",
      this.controller.lookHeading(IRobot.NORTH) == IRobot.WALL);
    assertTrue(
      "HomingController doesn't see a passage in the east!",
      this.controller.lookHeading(IRobot.EAST) == IRobot.PASSAGE);
    assertTrue(
      "HomingController doesn't see a wall in the south!",
      this.controller.lookHeading(IRobot.SOUTH) == IRobot.WALL);
    assertTrue(
      "HomingController doesn't see a passage in the west!",
      this.controller.lookHeading(IRobot.WEST) == IRobot.PASSAGE);
  }

  /*
    Tests whether the homing controller's determineHeading method
    works correctly.
  */
  @Test(timeout=10000)
  public void determineHeadingTest() {
    // Initialise variable to store heading
    int head = 0;

    // Block north and south with walls and move the target two
    // cells to the west to check the function returns west
    this.maze.setCellType(2, 1, Maze.WALL);
    this.maze.setCellType(2, 3, Maze.WALL);
    this.robot.setTargetLocation(new Point(0, 2));

    assertTrue(
      "HomingController doesn't choose to move west towards target",
      this.controller.determineHeading() == IRobot.WEST);

    // Move the target two cells east and two cells south and check
    // the function returns east
    this.robot.setTargetLocation(new Point(4, 4));

    assertTrue(
      "HomingController doesn't choose to move east towards target",
      this.controller.determineHeading() == IRobot.EAST);

    // Block east and west with walls and set north and south to passages
    // then check whether the function returns south
    this.maze.setCellType(2, 1, Maze.PASSAGE);
    this.maze.setCellType(2, 3, Maze.PASSAGE);
    this.maze.setCellType(1, 2, Maze.WALL);
    this.maze.setCellType(3, 2, Maze.WALL);

    assertTrue(
      "HomingController doesn't choose to move south towards target",
      this.controller.determineHeading() == IRobot.SOUTH);

    // Move the target two cells west and two cells north of the robot
    // to check whether the function returns north
    this.robot.setTargetLocation(new Point(0, 0));

    assertTrue(
      "HomingController doesn't choose to move north towards target",
      this.controller.determineHeading() == IRobot.NORTH);

    // Block the path south and check the function returns north
    this.maze.setCellType(2, 3, Maze.WALL);

    assertTrue(
      "HomingController selects a heading into a wall",
      this.controller.determineHeading() == IRobot.NORTH);

    // Block the path north and open the path east to check the
    // function returns east
    this.maze.setCellType(3, 2, Maze.PASSAGE);
    this.maze.setCellType(2, 1, Maze.WALL);

    assertTrue(
      "HomingController selects a heading into a wall",
      this.controller.determineHeading() == IRobot.EAST);

    // Block the path east and open the path south to check the
    // function returns south
    this.maze.setCellType(2, 3, Maze.PASSAGE);
    this.maze.setCellType(3, 2, Maze.WALL);

    assertTrue(
      "HomingController selects a heading into a wall",
      this.controller.determineHeading() == IRobot.SOUTH);

    // Block the path south and open the path west to check the
    // function returns west
    this.maze.setCellType(1, 2, Maze.PASSAGE);
    this.maze.setCellType(2, 3, Maze.WALL);

    assertTrue(
      "HomingController selects a heading into a wall",
      this.controller.determineHeading() == IRobot.WEST);

    // Move the target two cells east of the target and test
    // whether the function returns west
    this.robot.setTargetLocation(new Point(4, 2));

    assertTrue(
      "HomingController selects a heading into a wall",
      this.controller.determineHeading() == IRobot.WEST);

    // Open the path north and move the target north west of the
    // robot and check the function returns either north or west
    this.maze.setCellType(2, 1, Maze.PASSAGE);
    this.robot.setTargetLocation(new Point(0, 0));
    head = this.controller.determineHeading();

    assertTrue(
      "The robot doesn't choose between two directions",
      head == IRobot.NORTH || head == IRobot.WEST);

    // Open the paths south and east and block the paths north and
    // west then move the target south east of the robot and check
    // the function returns either south or east
    this.maze.setCellType(2, 3, Maze.PASSAGE);
    this.maze.setCellType(3, 2, Maze.PASSAGE);
    this.maze.setCellType(1, 2, Maze.WALL);
    this.maze.setCellType(2, 1, Maze.WALL);
    this.robot.setTargetLocation(new Point(4, 4));
    head = this.controller.determineHeading();

    assertTrue(
      "The robot doesn't choose between two directions",
      head == IRobot.SOUTH || head == IRobot.EAST);

    // Block path east with wall and move target north east of the
    // robot and test that the robot doesn't hit any walls
    this.maze.setCellType(3, 2, Maze.WALL);
    this.robot.setTargetLocation(new Point(3, 0));
    this.controller.start();
    assertTrue(
      "The robot collides with a wall",
      this.robot.getCollisions() == 0);
  }
}
