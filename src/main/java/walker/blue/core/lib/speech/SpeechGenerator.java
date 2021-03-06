package walker.blue.core.lib.speech;

import java.util.List;

import walker.blue.path.lib.node.GridNode;
import walker.blue.path.lib.node.RectCoordinates;

/**
 * Class in charge of generating the speech that will be spoken to the user
 */
public class SpeechGenerator {

    /**
     * delta value used to compare doubles
     */
    private static final double DELTA_DISTANCE = 1.0f;

    /**
     * Current path for the user
     */
    private List<GridNode> path;

    /**
     * Contructor. Sets the fields to the given values
     *
     * @param path Current path for the user
     */
    public SpeechGenerator(final List<GridNode> path) {
        this.path = path;
    }

    /**
     * Generates speech for the given nodes
     *
     * @param currentLocation Current node the user is located
     * @param nextWaypoint Next waypoint within the path
     * @return Speech generated that will be spoken to the user
     */
    public GeneratedSpeech getSpeechForNodes(final RectCoordinates currentLocation, final GridNode nextWaypoint) {
        final double distanceBetweenNodes = this.getDistance(currentLocation, nextWaypoint);
        final NodeEvent event = this.getEventAtNextNode(nextWaypoint);
        final NodeDirection direction = this.getDirectionForEvent(event, currentLocation, nextWaypoint);
        return new GeneratedSpeech(distanceBetweenNodes, event, direction);
    }

    /**
     * Calulates the direction at the given event with the given nodes
     *
     * @param event Type of event whose direction is being calculated
     * @param currentLocation Current node the user is located
     * @param nextWaypoint Next waypoint within the path
     * @return Direction of the given event
     */
    private NodeDirection getDirectionForEvent(final NodeEvent event,
                                               final RectCoordinates currentLocation,
                                               final GridNode nextWaypoint) {
        switch (event) {
            case TURN:
                return this.resolveDirectionForTurn(currentLocation, nextWaypoint);
            case REACHING_DESTINATION:
                return this.resolveDirectionForDestination(currentLocation, nextWaypoint);
            case REACHING_DESTINATION_AHEAD:
                return this.resolveDirectionForDestination(currentLocation, nextWaypoint);
        }
        return null;
    }

    /**
     * Calulates the direction at the given turn event with the given nodes
     *
     * @param userLocation Current node the user is located
     * @param nextWaypoint Next waypoint within the path
     * @return Direction of the given turn event
     */
    private NodeDirection resolveDirectionForTurn(final RectCoordinates userLocation, final GridNode nextWaypoint) {
        final int nextIndex = this.path.indexOf(nextWaypoint);
        final GridNode nextNextNode = this.path.get(nextIndex + 1);
        final int crossProduct = this.getNodeCrossProduct(userLocation, nextWaypoint, nextNextNode);
        if (crossProduct < 0) {
            return NodeDirection.LEFT;
        } else if (crossProduct > 0) {
            return NodeDirection.RIGHT;
        } else {
            return  NodeDirection.BEHIND;
        }
    }

    /**
     * Calulates the direction at the given destination based event with the
     * given nodes
     *
     * @param userLocation Current node the user is located
     * @param nextWaypoint Next waypoint within the path
     * @return Direction of the given turn event
     */
    private NodeDirection resolveDirectionForDestination(final RectCoordinates userLocation, final GridNode nextWaypoint) {
        final int nextIndex = this.path.indexOf(nextWaypoint);
        final GridNode nextNextNode = this.path.get(nextIndex + 1);
        final int crossProduct = this.getNodeCrossProduct(userLocation, nextWaypoint, nextNextNode);
        if (crossProduct < 0) {
            return NodeDirection.LEFT;
        } else if (crossProduct > 0) {
            return NodeDirection.RIGHT;
        } else {
            // We assume this always means ahead since we currently dont
            // have the ability to get the direction the user is facing
            // TODO: Handle NodeDirection.BEHIND
            return NodeDirection.AHEAD;
        }
    }

    /**
     * Gets the cross product of the vector from tail to mid crossed with the
     * vector from tail to head
     *
     * @param tail Beginning node
     * @param mid Middle node
     * @param head Final node
     * @return cross product of the vectors fromed by the nodes
     */
    private int getNodeCrossProduct(final GridNode tail, final GridNode mid, final GridNode head) {
        return this.getNodeCrossProduct(tail.getLocation(), mid.getLocation(), head.getLocation());
    }

    /**
     * Gets the cross product of the vector from tail to mid crossed with the
     * vector from tail to head
     *
     * @param tail Beginning node
     * @param mid Middle node
     * @param head Final node
     * @return cross product of the vectors fromed by the nodes
     */
    private int getNodeCrossProduct(final RectCoordinates tail, final GridNode mid, final GridNode head) {
        return this.getNodeCrossProduct(tail, mid.getLocation(), head.getLocation());
    }

    /**
     * Gets the cross product of the vector from tail to mid crossed with the
     * vector from tail to head
     *
     * @param tail Beginning node
     * @param mid Middle node
     * @param head Final node
     * @return cross product of the vectors fromed by the nodes
     */
    private int getNodeCrossProduct(final RectCoordinates tail,
                                    final RectCoordinates mid,
                                    final RectCoordinates head) {
        final int tailToHeadX = head.getX() - tail.getX();
        final int tailToHeadY = head.getY() - tail.getY();
        final int midToHeadX = head.getX() - mid.getX();
        final int midToHeadY = head.getY() - mid.getY();
        return this.crossProduct2D(tailToHeadX, tailToHeadY, midToHeadX, midToHeadY);
    }

    /**
     * Gets the event at the next node
     *
     * @param nextNode Next node in the path
     * @return Event at the given node
     */
    private NodeEvent getEventAtNextNode(final GridNode nextNode) {
        final int nextNodeIndex = this.path.indexOf(nextNode);
        if (nextNodeIndex == this.path.size() - 1) {
            return NodeEvent.REACHING_DESTINATION_AHEAD;
        } else {
            // Not on the last node yet
            final GridNode nextNextNode = this.path.get(nextNodeIndex + 1);
            if (this.getDistance(nextNextNode, nextNode) <= DELTA_DISTANCE) {
                return NodeEvent.REACHING_DESTINATION;
            } else {
                return NodeEvent.TURN;
            }
        }
    }

    /**
     * Calculates the distance between the two given nodes
     *
     * @param a  node a
     * @param b node b
     * @return the calculated distance between the nodes
     */
    private double getDistance(final GridNode a, final GridNode b) {
        return this.getDistance(a.getLocation(), b.getLocation());
    }

    /**
     * Calculates the distance between the two given nodes
     *
     * @param a  node a
     * @param b node b
     * @return the calculated distance between the nodes
     */
    private double getDistance(final RectCoordinates a, final GridNode b) {
        return this.getDistance(a, b.getLocation());
    }

    /**
     * Calculates the distance between the two given nodes
     *
     * @param a  node a
     * @param b node b
     * @return the calculated distance between the nodes
     */
    private double getDistance(final RectCoordinates a, final RectCoordinates b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

    /**
     * Calculates the 2D cross product of the vecotrs formed by the given values
     *
     * @param aX x value of vector a
     * @param aY y value of vector a
     * @param bX x value of vector b
     * @param bY y value of vector b
     * @return cross product between the vectors
     */
    private int crossProduct2D(final int aX, final int aY, final int bX, final int bY) {
        return (aX * bY) - (aY * bX);
    }
}
