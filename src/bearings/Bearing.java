/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bearings;

import java.util.Date;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Александр
 */
public class Bearing {
    private String name;
    private String type;
    private String producer;
    private double inDiameter;
    private double outDiameter;
    private double height;
    private final SimpleStringProperty inDate = new SimpleStringProperty();
    private final SimpleIntegerProperty count = new SimpleIntegerProperty();

    public Bearing(String name, String type, String producer, double inDiameter, double outDiameter, double height, String inDate, int count) {
        this.name = name;
        this.type = type;
        this.producer = producer;
        this.inDiameter = inDiameter;
        this.outDiameter = outDiameter;
        this.height = height;
        this.inDate.set(inDate);
        this.count.set(count);
    }
    
    public SimpleIntegerProperty countProperty() {return count;}
    
    public SimpleStringProperty inDateProperty() {return inDate;}

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getProducer() {
        return producer;
    }

    public double getInDiameter() {
        return inDiameter;
    }

    public double getOutDiameter() {
        return outDiameter;
    }

    public double getHeight() {
        return height;
    }
}
