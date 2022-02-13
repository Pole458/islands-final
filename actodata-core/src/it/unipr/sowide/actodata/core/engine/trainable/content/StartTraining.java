package it.unipr.sowide.actodata.core.engine.trainable.content;

import it.unipr.sowide.actodata.core.dataflow.Data;

import java.io.Serializable;

public class StartTraining<TI, TP> implements Serializable {
    private final Data<TI> input;
    private final TP trainingParameters;

    public StartTraining(Data<TI> input, TP trainingParameters) {
        this.input = input;
        this.trainingParameters = trainingParameters;
    }

    public Data<TI> getInput(){
        return input;
    }
    public TP getTrainingParameters(){
        return trainingParameters;
    }

    @Override
    public String toString() {
        return "StartTraining{" +
                "input=" + input +
                ", trainingParameters=" + trainingParameters +
                '}';
    }
}
