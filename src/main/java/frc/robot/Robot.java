/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.RobotDrive;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends IterativeRobot {
  
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  //Joystick, VictorSP, Spark, DigitalInput DoubleSolenoid, Camera



  Spark left1, left2, right1, right2;
  VictorSP inLeft, inRight, arm, lowWheel1, lowWheel2;
  Joystick joy0, joy1;
  RobotDrive drive; 
  DigitalInput topSwitch, bottomSwitch;
  DoubleSolenoid latchPusher, liftBack, liftFront;

  double magnitude, rotation;
  boolean topPressed, bottomPressed;


  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    
    //Construct the drive motors
    left1 = new Spark(8);
    left2 = new Spark(7);
    right1 = new Spark(6);
    right2 = new Spark(5);

    //Construct the intake motors
    inLeft = new VictorSP(0);
    inRight = new VictorSP(1);
    //Construct the arm (up/down) motor
    arm = new VictorSP(4);
    //Construct the drive wheels on the lift, that move the robot
    //when the regular drivetrain is lifted off the ground
    lowWheel1 = new VictorSP(3);
    lowWheel2 = new VictorSP(2);

    //Construct the two joysticks
    joy0 = new Joystick(0);
    joy1 = new Joystick(1);

    //Construct the limit switches for the arm
    topSwitch = new DigitalInput(2);
    bottomSwitch = new DigitalInput(3);

    //Construct the drivetrain
    drive = new RobotDrive(left1, left2, right1, right2);

    //Construct the 3 solenoids on the robot
    latchPusher = new DoubleSolenoid(0, 3); //this one solenoid controls the 3 air 
                                            //cylinders in the hatch mechanism
    liftBack = new DoubleSolenoid(1, 4);
    liftFront = new DoubleSolenoid(2, 5);

    //Code to send video to the driver station
    CameraServer.getInstance().startAutomaticCapture(0);
    CameraServer.getInstance().startAutomaticCapture(1);
    


  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // autoSelected = SmartDashboard.getString("Auto Selector",
    // defaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {

//DRIVE 

    magnitude = -1 * joy0.getRawAxis(1);
    rotation = -1 * joy0.getRawAxis(4);

    drive.arcadeDrive(magnitude, rotation, true);


//INTAKE

    if(joy1.getRawButton(6)){
      inLeft.set(-.5);
      inRight.set(.5);
      // right bumper when held starts intake
    }
    else if(joy1.getRawButton(5)){
      inLeft.set(1);
      inRight.set(-1);
      //left bumper when held starts outtake
    }
    else{
      inLeft.set(-0.05);
      inRight.set(0.05);
      // when nothing is pressed, wheels roll inwards slowly
    }



// ARM ANGLE

    if(joy1.getRawButton(4)){ //button Y
      if(topSwitch.get()){
        arm.set(0.1);
        //stops arm angle motor at top
      }
      else{
        arm.set(.7); // set to 0.7 because it is working against gravity
        //arm angle goes up when Y is held
      }
    }
    else if(joy1.getRawButton(1)){ //button A
      if(bottomSwitch.get()){
        arm.set(0);
        //stops arm angle motor at bottom
      }
      else{
        arm.set(-.4);// set to 0.4 because its working with gravity
      }
    }
    else {
      arm.set(0);
    }



// HATCH MECHANISM

    if(joy1.getRawButton(2)){ //button B
      latchPusher.set(Value.kForward);
    }
    else {
      latchPusher.set(Value.kReverse);
    } //when B is held, actuators are out, otherwise they are in

    

// CLIMB MECHANISM

    if(joy0.getRawButton(6)){// right bumper
      liftBack.set(Value.kForward);
    }
    else {
      liftBack.set(Value.kReverse);
    }//holding deploys back lift

    if(joy0.getRawButton(5)){// left bumper
      liftFront.set(Value.kForward);
    }
    else {
      liftFront.set(Value.kReverse);
    }//holding deploys front lift 

              if(joy0.getRawButton(4) && joy0.getRawButton(6) ){ 
                lowWheel1.set(.69); //nice
                lowWheel2.set(.69); //nice
              }
              else {
                lowWheel1.set(0);
                lowWheel2.set(0);
              } //if back lift is deployed and Y is pressed, Y drives low wheels forward

  
    
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}