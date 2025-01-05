import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class WorkoutManager {
    private ObjectMapper objectMapper;

    public WorkoutManager() {
        objectMapper = new ObjectMapper();
    }

    public Workout loadWorkout(String filePath) throws IOException {
        Workout workout = objectMapper.readValue(new File(filePath), Workout.class);
        if (workout.getBlocks() == null) {
            workout.setBlocks(new ArrayList<>());
        }
        for (Block block : workout.getBlocks()) {
            if (block.getExercises() == null) {
                block.setExercises(new ArrayList<>());
            }
        }
        return workout;
    }


    public void saveWorkout(String filePath, Workout workout) throws IOException {
        if (workout.getBlocks() == null) {
            workout.setBlocks(new ArrayList<>()); // Ensure non-null blocks
        }
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), workout);
    }


    public Workout createNewWorkout() {
        Workout workout = new Workout();
        workout.setId("NewWorkout");
        workout.setName("New Workout");
        workout.setCompleted(false);
        workout.setBlocks(new ArrayList<>());
        return workout;
    }
}
