package org.usfirst.frc.team1318.robot.driver.autonomous;

import org.usfirst.frc.team1318.robot.TuningConstants;
import org.usfirst.frc.team1318.robot.common.IDashboardLogger;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.IDigitalInput;
import org.usfirst.frc.team1318.robot.driver.IControlTask;
import org.usfirst.frc.team1318.robot.driver.controltasks.ConcurrentTask;
import org.usfirst.frc.team1318.robot.driver.controltasks.DriveRouteTask;
import org.usfirst.frc.team1318.robot.driver.controltasks.DriveVelocityTimedTask;
import org.usfirst.frc.team1318.robot.driver.controltasks.IntakeExtendTask;
import org.usfirst.frc.team1318.robot.driver.controltasks.PIDBrakeTask;
import org.usfirst.frc.team1318.robot.driver.controltasks.SequentialTask;
import org.usfirst.frc.team1318.robot.driver.controltasks.ShooterKickerTask;
import org.usfirst.frc.team1318.robot.driver.controltasks.ShooterSpinDownTask;
import org.usfirst.frc.team1318.robot.driver.controltasks.ShooterSpinUpTask;
import org.usfirst.frc.team1318.robot.driver.controltasks.StingerTask;
import org.usfirst.frc.team1318.robot.driver.controltasks.TurnOneShotTask;
import org.usfirst.frc.team1318.robot.driver.controltasks.WaitTask;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class AutonomousRoutineSelector
{
    // smartdash other constants
    public static final String LogName = "auto";

    private final IDashboardLogger logger;

    // DipSwitches for selecting autonomous mode
    private final IDigitalInput dipSwitchA;
    private final IDigitalInput dipSwitchB;
    private final IDigitalInput dipSwitchC;

    /**
     * Initializes a new AutonomousDriver
     */
    @Inject
    public AutonomousRoutineSelector(
        IDashboardLogger logger,
        @Named("AUTO_DIP_SWITCH_A") IDigitalInput dipSwitchA,
        @Named("AUTO_DIP_SWITCH_B") IDigitalInput dipSwitchB,
        @Named("AUTO_DIP_SWITCH_C") IDigitalInput dipSwitchC)
    {
        this.logger = logger;

        this.dipSwitchA = dipSwitchA;
        this.dipSwitchB = dipSwitchB;
        this.dipSwitchC = dipSwitchC;
    }

    /**
     * Check what routine we want to use and return it
     * 
     * @return autonomous routine to execute during autonomous mode
     */
    public IControlTask selectRoutine()
    {
        int routineSelection = 0;
        if (!this.dipSwitchA.get())
        {
            routineSelection += 1;
        }

        if (!this.dipSwitchB.get())
        {
            routineSelection += 2;
        }

        if (!this.dipSwitchC.get())
        {
            routineSelection += 4;
        }

        // add next base2 number (1, 2, 4, 8, 16, etc.) here based on number of
        // dipswitches and which is on...

        // print routine selection to the smartdash
        this.logger.logInteger(AutonomousRoutineSelector.LogName, "routine", routineSelection);

        switch (routineSelection)
        {
            case 0: // No switches flipped
                return AutonomousRoutineSelector.GetFillerRoutine();

            case 1: // Switch A flipped
                return AutonomousRoutineSelector.GetDriveTimedRoutine(
                    TuningConstants.AUTONOMOUS_TIME_SLOW,
                    0.0,
                    TuningConstants.DRIVETRAIN_AUTONOMOUS_SLOW_VELOCITY);

            case 2: // Switch B flipped
                return AutonomousRoutineSelector.GetDriveTimedRoutine(
                    TuningConstants.AUTONOMOUS_TIME_FAST,
                    0.0,
                    TuningConstants.DRIVETRAIN_AUTONOMOUS_FAST_VELOCITY);

            case 3: // Switches A and B flipped
                return AutonomousRoutineSelector.GetPortcullisBreachRouteRoutine();

            case 4: // Switch C flipped
                return AutonomousRoutineSelector.GetDriveStraightAndTurnAndShootCloseRouteRoutine();

            case 5: // Switches A and C flipped
                return AutonomousRoutineSelector.GetChevalDeFriseBreachRouteRoutine();

            case 6: // Switches B and C flipped
                return AutonomousRoutineSelector.GetDriveStraightAndCurveAndShootCloseRouteRoutine();

            case 7: // Switches A and B and C flipped
                return AutonomousRoutineSelector.GetDriveStraightAndTurnAndReaproachAndShootCloseRouteRoutine();

            default: // CANNOT READ
                return AutonomousRoutineSelector.GetFillerRoutine();
        }
    }

    /**
     * Gets an autonomous routine that does nothing
     * 
     * @return very long WaitTask
     */
    private static IControlTask GetFillerRoutine()
    {
        return new WaitTask(0);
    }

    /**
     * Gets an autonomous routine that moves the specified velocity for the
     * specified time
     * 
     * @param time
     *            - time to drive forward
     * @param xVelocity
     *            - velocity in the x to maintain while driving
     * @param yVelocity
     *            - velocity in the y to maintain while driving
     * @return DriveTimedTask of specified time, and x and y velocities
     */
    private static IControlTask GetDriveTimedRoutine(double time, double xVelocity, double yVelocity)
    {
        return SequentialTask.Sequence(
            new WaitTask(8.0),
            ConcurrentTask.AllTasks(
                new IntakeExtendTask(0.5, true),
                new DriveVelocityTimedTask(time, xVelocity, yVelocity)));
    }

    private static IControlTask GetChevalDeFriseBreachRouteRoutine()
    {
        return SequentialTask.Sequence(
            ConcurrentTask.AllTasks(
                new IntakeExtendTask(TuningConstants.SHOOTER_LOWER_KICKER_DURATION, true),
                new DriveRouteTask(
                        (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 136.0 : 136.0,
                        (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 136.0 : 136.0,
                        2.0)),
            new StingerTask(1.0, true),
            ConcurrentTask.AllTasks(
                new StingerTask(1.0, true),
                new DriveRouteTask(
                        (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 350.0 : 350.0,
                        (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 350.0 : 350.0,
                        5.0)));
    }

    private static IControlTask GetPortcullisBreachRouteRoutine()
    {
        return SequentialTask.Sequence(
            ConcurrentTask.AllTasks(
                new IntakeExtendTask(0.5, true),
                new StingerTask(0.5, true)),
            SequentialTask.Sequence(
                ConcurrentTask.AllTasks(
                    new DriveRouteTask(
                            (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 250.0 : 250.0,
                            (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 250.0 : 250.0,
                            2.5),
                    new StingerTask(2.5, true)),
                new DriveRouteTask(
                        (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 250.0 : 250.0,
                        (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 250.0 : 250.0,
                        2.5)),
            new StingerTask(1.0, false));
    }

    private static IControlTask GetDriveStraightAndTurnAndShootCloseRouteRoutine()
    {
        return SequentialTask.Sequence(
            ConcurrentTask.AllTasks(
                new IntakeExtendTask(TuningConstants.SHOOTER_LOWER_KICKER_DURATION, true),
                new DriveRouteTask(
                        (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 585.0 : 585.0, // 525.0
                        (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 585.0 : 585.0, // 525.0
                        7.25)), // 7.0
            ConcurrentTask.AllTasks(
                SequentialTask.Sequence(
                    new TurnOneShotTask(75.0, false), // 68.0
                    new WaitTask(0.5)),
                new IntakeExtendTask(TuningConstants.SHOOTER_LOWER_KICKER_DURATION, false)),
            new DriveRouteTask(
                    (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 400.0 : 400.0, // 420.0
                    (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 400.0 : 400.0, // 420.0
                    3.25), // 3.5
            ConcurrentTask.AnyTasks(
                SequentialTask.Sequence(
                    new ShooterKickerTask(TuningConstants.SHOOTER_LOWER_KICKER_DURATION, true),
                    new ShooterSpinUpTask(false, TuningConstants.SHOOTER_CLOSE_SHOT_VELOCITY,
                            TuningConstants.SHOOTER_SPIN_UP_DURATION),
                    new ShooterKickerTask(TuningConstants.SHOOTER_FIRE_DURATION, false),
                    new ShooterSpinDownTask(TuningConstants.SHOOTER_REVERSE_DURATION)),
                new PIDBrakeTask()));
    }

    private static IControlTask GetDriveStraightAndTurnAndReaproachAndShootCloseRouteRoutine()
    {
        return SequentialTask.Sequence(
            ConcurrentTask.AllTasks(
                new IntakeExtendTask(TuningConstants.SHOOTER_LOWER_KICKER_DURATION, true),
                new DriveRouteTask(
                        (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 585.0 : 585.0, // 525.0
                        (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 585.0 : 585.0, // 525.0
                        7.0)),
            ConcurrentTask.AllTasks(
                SequentialTask.Sequence(
                    new DriveRouteTask(
                            (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 95.0 : 95.0, // 420.0
                            (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 0.0 : 0.0, // 420.0
                            .75),
                    // new TurnTask(75.0, false), //68.0
                    new WaitTask(0.3)),
                new IntakeExtendTask(TuningConstants.SHOOTER_LOWER_KICKER_DURATION, false)),
            new DriveRouteTask(
                    (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 400.0 : 400.0, // 400
                    (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 400.0 : 400.0, // 400
                    3.10),
            new WaitTask(0.70),
            new DriveRouteTask(
                    (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 100.0 : 100.0,
                    (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 100.0 : 100.0,
                    0.70),
            ConcurrentTask.AnyTasks(
                SequentialTask.Sequence(
                    new ShooterKickerTask(TuningConstants.SHOOTER_LOWER_KICKER_DURATION, true),
                    new ShooterSpinUpTask(false, TuningConstants.SHOOTER_CLOSE_SHOT_VELOCITY,
                            TuningConstants.SHOOTER_SPIN_UP_DURATION),
                    new ShooterKickerTask(TuningConstants.SHOOTER_FIRE_DURATION, false),
                    new ShooterSpinDownTask(TuningConstants.SHOOTER_REVERSE_DURATION)),
                new PIDBrakeTask()));
    }

    private static IControlTask GetDriveStraightAndCurveAndShootCloseRouteRoutine()
    {
        return SequentialTask.Sequence(
            ConcurrentTask.AllTasks(
                new IntakeExtendTask(TuningConstants.SHOOTER_LOWER_KICKER_DURATION, true),
                new DriveRouteTask(
                        (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 175.0 : 175.0, // 250.0
                        (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 175.0 : 175.0, // 250.0
                        4.0)),
            new DriveRouteTask(
                    (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 500.0 : 500.0, // 490.0
                    (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 420.0 : 420.0, // 420.0
                    4.0),
            new DriveRouteTask(
                    (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 200.0 : 200.0, // 200.0
                    (timeRatio) -> timeRatio < 0.9 ? timeRatio / 0.9 * 200.0 : 200.0, // 200.0
                    3.0),
            ConcurrentTask.AnyTasks(
                SequentialTask.Sequence(
                    new ShooterKickerTask(TuningConstants.SHOOTER_LOWER_KICKER_DURATION, true),
                    new ShooterSpinUpTask(false, TuningConstants.SHOOTER_CLOSE_SHOT_VELOCITY,
                            TuningConstants.SHOOTER_SPIN_UP_DURATION),
                    new ShooterKickerTask(TuningConstants.SHOOTER_FIRE_DURATION, false),
                    new ShooterSpinDownTask(TuningConstants.SHOOTER_REVERSE_DURATION)),
                new PIDBrakeTask()));
    }
}

/*
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * . .;+;+ .+;;' `,+'. ;';;+:..`` :+'+ ,'+` .+;;;;;+ ;,,, .+;;;;;'+++; ;'
 * `+;;;;;#+'+'+''#:. '`+';;;'+;+;+++'''+'. #';;;;#';+'+'''+''+'
 * ;;;;#;,+;;+;;;'''''': ';'++'.`+;;'';;''+'', :#'#+'``.'+++'#++'':`
 * `';++##```##+.''.## +++# #`# `++++ +'#+ # :#: # ##'+ `#+# +`+ #'#`
 * :,.+,+,`:+,+.., `,:```,`,`.`;, :+.;``.``;.#; .'``'+'+'``'. ,````````..
 * :```````: +``.:,``' :```````: +`````+ ';+## '```' `'```'` .+''''''''
 * +;;;;;;;;''# : ` `: `, ' + ' ,;';,``.``.,,,:;# +;;;;;;;;;;;;;;;'
 * ,';;;;;;;;;;;;;;;', +:;;;;;;';;;;;;;;;+ `. .:,;+;;:::;.``, :` #, `.` + # ; .;
 * .;;,` , `,+ +;;;;;;''';;;;;;;';;'; +;;;;;;;';;;;;;;;;;'';;
 * `';;;;;;';;;;;;;;;;;';;+ + `:;;;;+;;;;;;;;';''':: ' `: ``````` , , : ' ; + '`
 * .. , , ,;;;;;..+,` ```.':;', +;;;;;;'+;;;;;;;;;;;;;;+;;;+
 * ';;;;;;++;;;;;;;;;;;;;;';;;+ `.:';;;;;#;;;;;;;;;;;;;;';;;;` ; `,;
 * ',:;;';;';;;;;:;`` + + ; ; ; ` ; : + ' `; ';:`` `` ' :`,:;;+ `';;;;'+
 * +,..```````..:;#;;;;;;. `;;;;;;+ +;;;;;;;;;;;;;':';;;;;# .;;;;;;+
 * ';;;;;;;;;;;;;;,';;;;` . : `.;;'+ +;;;;;;;;;;;;;','.` + ' ; +.,,;:;:;;;,..`:
 * , `` + , ' : ; .;'+ +.` `` + ; ;:;;;;': ';;;';;` + .' ;;;;;;;+ ';;;;;'
 * :+++#++##+#+''', +;;;;.`. +;;;;;' +;;::;;;+:+;;'', ,;;. + ``:;;;;+
 * +;;:;;;:+;+;;++; + .` ` ``' +;;;;;;;+;+;;'+; , ;#, . ; ';;;;;;;;;;;;++' +
 * .+``.; `` ; ';;;;;;+;';;;'+' #`````:, +++;,:. ':;''++;:';:;''; +``````,`
 * ,```,+ +;;';:;;+;;;;''; +``````,+ .``````: ;:;;++';;;;;;';, ,``:#``+`.
 * ,``````' `';;;;:;;;;;;+;` '+``+:'`..' ,``````' +;;;;;;;;;;;'' ;:'``#;;.`++
 * ```````; `;:;;;;;;;;;;# ':'``++:+`+; ```'`.`; +;;;;;;;;;;;+ :::#``' +#`';
 * ,``'`:`# `';;;;;;;;;;+ +:'.`,. ++`;; +`.``+`' :#;;;;;;;;;;;` +:# ,` +;`.'
 * ,.`+`.:. ##;;;;;;;;;;;' ,'` ;:+# '`;.`+`# ##+;;;;;;;;;;+ ,::; ,+,`:``,
 * :###;;;;;;;;;:' +:;` '`,,`+ ';##';;;;;;;;;;. +:# '+.+ +;;##;;;;;;;;;;' ;:; `
 * :;;;+#;;;;;;;;;;+ ;::` +;;;;#+;;;;;;;;;; +:' ';;;;+#;;;;;;;;;;. ;:'
 * ,;;;;;;#;;;;;;;;;;+ +::. +;;;;;;'';;;;;;;;;' +:+ `;;;;;;;;#;;;;;;;;;;` `;:+
 * ,;;;;;;;;+;;;;;;;;;;+ ':;, +;;;;;;;;;+;;;;;;;;;' +:+ .;;;;;;;;;+,;;;;;;;;;;`
 * ;;+ ';;;;;;;;;, ';;;;;;:;;, +;:, ';;;;;;;;' +;;;;;;;;;' +:+ ;;;;;;;;;;+
 * ,;;;;;;;;;+ ;:' +;;;;;;;;; ';;;;;;;;;`;:;` ;;;;;;;;;+ +;;;;;;;;;+#:+
 * ';;;;;;;;;: ;;;;;;;;;;';:' `';;;;;;;:' ';;;;;;;;;;:. .;;;;;;;;;+ +;;;;;;;;;'+
 * +;;;;;;;;; ';;;;;;;;;#+ `;;;;;;;;;+ `;;;;;;;;;;` +;;;;;;;;;. +;;;;;;;;;`
 * ';;;;;;;:' ;;;;;;;;;;; :;;;;;;;;;: `;;;;;;;;;+ +;;;;;;;;; ';;;;;;;;;`
 * ;;;;;;;;;+ ';;;;;;;;;: ';;;;;;;;;; ,;;;;;;;;;+ ':;;;;;;;' +;;;;;;;;;
 * .;:;;;;;;;' +;;;;;;;;;: +;;;;;;;;;` .;;;;;;;;;+ `;;;;;;;;;+ ;:;;;;;;;;`
 * ;;;;;;;;;;. +;;;;;;;::. ';;;;;;;;'` :;;;;;;;;:+ :;;;;;;;;:' ';;;;;;;;;'
 * ';;;;;;;;'` +#;;;;;;;;;` `;;;;;;;;;+ '';;;;;;;;;+ +;;;;;;;;;. '::;;;;;;;;;+
 * ;;;;;;;;;+ #:'';;;;;;;;;` .#;;;;;;;;' `;:+;;;;;;;;;;; ':'';;;;;;
 * '::.,;;;;;;;;;+ +::::+';;;+ ':' +:;;;;;;;;` `;;;::::;#+: `;:+ +;;;;;;;:;;
 * '#+, +#::::::::;'` +:;, `;;;;:;;'#';;;;;::;:'` ;:''::::::::#` +:'
 * ';:;;+'::;;:;::::::'' +::;+':::::::'. .:;+ '''+;::;:;:::;:::;':'
 * ';;:;'';:::::': +::. +:::::::::::::;#;:# .''##;#;:;;:::'+ `+;'
 * ;:;::::::::;'+;:'+ ` `+:;+:;::;::+ +:;# ';:::;:+#+';:::+. ,+::+#';::;+ ';::
 * #:;;'+';'''++:` '':::;'''# ,:;;` #';:;;:+ `:'++;;': :++ .;;:;;#, ` '':``
 * 
 * 
 */
