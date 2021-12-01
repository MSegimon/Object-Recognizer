import Object
from betterImage import enhance


def main(path):
    #Enhance the image
    enhance(path)

    #Get objects in the image
    objects = Object.GetObjects(path)

    #Print the objects
    for object in objects:
        if object.HasGoodConfidence():
            print("There is a " + object.GetName() + " to your ", end="")
        else:
            print("There is an unknown object to your ", end="")

        pos = object.GetRelativePosition()
        if pos == Object.Direction.LEFT:
            print("left")
        elif pos == Object.Direction.SLIGHT_LEFT:
            print("slight left")
        elif pos == Object.Direction.RIGHT:
            print("right")
        elif pos == Object.Direction.SLIGHT_RIGHT:
            print("slight right")
        elif pos == Object.Direction.CENTER:
            print("center")



if __name__ == '__main__':
    #For testing purposes we duplicate the image and run the program on the duplicate
    import os
    image = "LivingRoom2.jpg"
    os.system("cp TestImages/" + image + " Duplicate" + image)

    #Run the program
    main("Duplicate" + image)

    #Delete the duplicate image after testing
    os.system("rm Duplicate" + image)
