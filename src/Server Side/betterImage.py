import PIL
from PIL import ImageEnhance

def enhance(path):
    """
    Enhance the image so that the objects are more visible.

    Args:
    path: The path to the image.  
    """
    img = PIL.Image.open(path)

    enhancer = ImageEnhance.Contrast(img)
    img = enhancer.enhance(3)

    enhancer = ImageEnhance.Color(img)
    img = enhancer.enhance(0.75)

    enhancer = ImageEnhance.Sharpness(img)
    img = enhancer.enhance(1.5)

    enhancer = ImageEnhance.Brightness(img)
    img = enhancer.enhance(0.75)    

    img.save(path)
