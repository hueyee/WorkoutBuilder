import java.util.List;

public class Workout {
    private String id;
    private String name;
    private boolean completed;
    private List<Block> blocks;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public List<Block> getBlocks() { return blocks; }
    public void setBlocks(List<Block> blocks) { this.blocks = blocks; }
}

class Block {
    private String blockName;
    private String type; // "set-based" or "simple"
    private List<Exercise> exercises;

    // Getters and setters
    public String getBlockName() { return blockName; }
    public void setBlockName(String blockName) { this.blockName = blockName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public List<Exercise> getExercises() { return exercises; }
    public void setExercises(List<Exercise> exercises) { this.exercises = exercises; }
}

class Exercise {
    private String name;
    private String duration;
    private List<Set> sets;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public List<Set> getSets() { return sets; }
    public void setSets(List<Set> sets) { this.sets = sets; }
}

class Set {
    private String percent;
    private String source;
    private String reps;
    private String loadPercent;
    private boolean failed;
    private String weight;

    // Getters and setters
    public String getPercent() { return percent; }
    public void setPercent(String percent) { this.percent = percent; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getReps() { return reps; }
    public void setReps(String reps) { this.reps = reps; }

    public String getLoadPercent() { return loadPercent; }
    public void setLoadPercent(String loadPercent) { this.loadPercent = loadPercent; }

    public boolean isFailed() { return failed; }
    public void setFailed(boolean failed) { this.failed = failed; }

    public String getWeight() { return weight; }
    public void setWeight(String weight) { this.weight = weight; }
}
