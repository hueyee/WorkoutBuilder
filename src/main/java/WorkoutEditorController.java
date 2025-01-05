import java.io.File;
import java.io.IOException;

public class WorkoutEditorController {
    private WorkoutManager workoutManager;
    private Workout currentWorkout;

    public WorkoutEditorController(WorkoutManager workoutManager) {
        this.workoutManager = workoutManager;
    }

    public void loadWorkout(File file) throws IOException {
        currentWorkout = workoutManager.loadWorkout(file.getAbsolutePath());
    }

    public void saveWorkout(File file) throws IOException {
        workoutManager.saveWorkout(file.getAbsolutePath(), currentWorkout);
    }

    public Workout getCurrentWorkout() {
        return currentWorkout;
    }

    public void setCurrentWorkout(Workout workout) {
        this.currentWorkout = workout;
    }

    public Workout createNewWorkout() {
        currentWorkout = workoutManager.createNewWorkout();
        return currentWorkout;
    }
}
