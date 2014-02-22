/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.namal.needless.compass.model.v2.cloudmade;

/**
 * http://cloudmade.com/documentation/routing#JSON-Parameters
 *
 * @author nmalik
 */
public class RouteInstruction {
    private String instruction;
    private long length;
    private long position;
    private long time;
    private String length_caption;
    private String earth_direction;
    private long azimuth;
    private String turn_type;
    private double turn_angle;

    /**
     * Text instruction.
     *
     * @return the instruction
     */
    public String getInstruction() {
        return instruction;
    }

    /**
     * @param instruction the instruction to set
     */
    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    /**
     * Length of the segment in meters.
     *
     * @return the length
     */
    public long getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(long length) {
        this.length = length;
    }

    /**
     * Index of the first point of the segment in route_geometry.
     *
     * @return the position
     */
    public long getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(long position) {
        this.position = position;
    }

    /**
     * Estimated time required to travel the segment in seconds.
     *
     * @return the time
     */
    public long getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * Length of the segments in specified units e.g. 22m, 23.4 km, 14.4 miles.
     *
     * @return the length_caption
     */
    public String getLengthCaption() {
        return length_caption;
    }

    /**
     * @param length_caption the length_caption to set
     */
    public void setLengthCaption(String length_caption) {
        this.length_caption = length_caption;
    }

    /**
     * Earth direction code of the start of the segment (now only 8 directions are supported, N, NE, E, SE, S, SW, W,
     * NW). http://en.wikipedia.org/wiki/File:Compass_Rose_English_Southeast.svg
     *
     * @return the earth_direction
     */
    public String getEarthDirection() {
        return earth_direction;
    }

    /**
     * @param earth_direction the earth_direction to set
     */
    public void setEarthDirection(String earth_direction) {
        this.earth_direction = earth_direction;
    }

    /**
     * North based azimuth. http://en.wikipedia.org/wiki/Azimuth
     *
     * @return the azimuth
     */
    public long getAzimuth() {
        return azimuth;
    }

    /**
     * @param azimuth the azimuth to set
     */
    public void setAzimuth(long azimuth) {
        this.azimuth = azimuth;
    }

    /**
     * Code of the turn type, optional, absent for the first segment.
     *
     * turn_type parameter can be:
     * <pre>
     * C	continue (go straight)
     * TL	turn left
     * TSLL	turn slight left
     * TSHL	turn sharp left
     * TR	turn right
     * TSLR	turn slight right
     * TSHR	turn sharp right
     * TU	U-turn
     * </pre>
     *
     * @return the turn_type
     */
    public String getTurnType() {
        return turn_type;
    }

    /**
     * @param turn_type the turn_type to set
     */
    public void setTurnType(String turn_type) {
        this.turn_type = turn_type;
    }

    /**
     * Angle in degress of the turn between two segments, 0 for go straight, 90 for turn right, 270 for turn left, 180
     * for U-turn.
     *
     * @return the turn_angle
     */
    public double getTurnAngle() {
        return turn_angle;
    }

    /**
     * @param turn_angle the turn_angle to set
     */
    public void setTurnAngle(double turn_angle) {
        this.turn_angle = turn_angle;
    }
}
