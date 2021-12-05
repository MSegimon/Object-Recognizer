import Object
from betterImage import enhance


def main(path):
    #Enhance the image
    enhance(path)

    #Get objects in the image
    objects = Object.GetObjects(path)

    #Create string that will be sent to the client
    output = ""
    for object in objects:
        if object.HasGoodConfidence():
            output += "There is a " + object.GetName() + " to your "
        else:
            output += "There is an unknown object to your "

        pos = object.GetRelativePosition()
        if pos == Object.Direction.LEFT:
            output += ("left. ")
        elif pos == Object.Direction.SLIGHT_LEFT:
            output += ("slight left. ")
        elif pos == Object.Direction.RIGHT:
            output += ("right. ")
        elif pos == Object.Direction.SLIGHT_RIGHT:
            output += ("slight right. ")
        elif pos == Object.Direction.CENTER:
            output += ("center. ")

    #Return string to client
    return output


runFiles = 'TestImages/'

if __name__ == '__main__':
    string = main(runFiles + 'LivingRoom2.jpg')
    print(string)
