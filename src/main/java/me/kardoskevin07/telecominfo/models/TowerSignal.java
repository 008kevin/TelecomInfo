package me.kardoskevin07.telecominfo.models;

import com.dbteku.telecom.models.CellTower;
import com.dbteku.telecom.models.WorldLocation;

public class TowerSignal {

    public CellTower cellTower;
    public WorldLocation worldLocation;
    public double strength;
    public TowerSignal(CellTower cellTower, WorldLocation worldLocation) {
        this.cellTower = cellTower;
        this.worldLocation = worldLocation;
        strength = cellTower.determineStrength(worldLocation);
    }

    @Override
    public String toString() {
        return cellTower.getType() + cellTower.determineStrength(worldLocation);
    }
}
