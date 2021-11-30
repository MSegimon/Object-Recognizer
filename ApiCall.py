import io

from credentials.setGoogleCredentials import setGoogleCredentials

def FindObjects(path):
    """Finds all the objects in the image at the given path
    
    Args:
    path: path to image file
    """

    #Set google credentials
    from credentials.setGoogleCredentials import setGoogleCredentials
    setGoogleCredentials()

    # Instantiates a client and reads image from path
    from google.cloud import vision
    client = vision.ImageAnnotatorClient()

    try:
        with io.open(path, 'rb') as image_file:
            content = image_file.read()
    except:
        print("Error opening file")
        return
    image = vision.Image(content=content)

    #Get objects from image
    objects = client.object_localization(image=image).localized_object_annotations

    """     for object_ in objects:
        print(object_.name)
        print(object_.score)
        print(object_.bounding_poly.normalized_vertices)
    """
    
    return objects

#FindObjects('TestImages/LivingRoom1.jpeg')
