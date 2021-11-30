from enum import Enum

import ApiCall


class Direction(Enum):
    RIGHT = 1,
    SLIGHT_RIGHT = 2,
    CENTER = 3,
    SLIGHT_LEFT = 4,
    LEFT = 5

class Object():
    """
    Object class is used to store the data and interact with the detected objects of an image.
    """
    def __init__(self, vertices, objectType, confidence):
        self.objectType = objectType
        self.confidence = confidence
        self.vertices = []

        for vertex in vertices:
            self.vertices.append([vertex.x, vertex.y])

    def GetRelativePosition(self):
        """
        Returns position relative to the position of the camera
        :return: The relative position of the object.
        """

        #Find horizontal center of the object
        center = self.vertices[0][0] + (self.vertices[1][0] - self.vertices[0][0])

        #Set the direction of the object (Values can and should be tweaked)
        if center > 0 and center <= 0.2:
            return Direction.LEFT
        elif center > 0.2 and center <= 0.4:
            return Direction.SLIGHT_LEFT
        elif center > 0.4 and center <= 0.6:
            return Direction.CENTER
        elif center > 0.6 and center <= 0.8:
            return Direction.SLIGHT_RIGHT
        elif center > 0.8 and center <= 1:
            return Direction.RIGHT
        


def GetRelativeObjectPositions(path):
    """
    Outputs the a list of the objects found in the image.

    Args:
    path: path to image file
    """

    #Generate list of objects
    resultObjects = ApiCall.FindObjects(path)
    objects = []
    for object in resultObjects:
        objects.append(Object(object.bounding_poly.normalized_vertices, object.name, object.score))

    #Filter objects
    for object in objects:
        if object.confidence < 0.5:
            objects.remove(object)

    return objects


    

if __name__ == '__main__':
    GetRelativeObjectPositions("TestImages/LivingRoom1.jpeg")
