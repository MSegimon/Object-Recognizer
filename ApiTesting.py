import os, os.path
import io
from PIL import Image, ImageDraw

#Other imports
import ApiCall

def main():
    testImages = os.listdir("TestImages")
    for image in testImages:
        #Get image objects
        objects = ApiCall.FindObjects("TestImages/" + image)

        #Create duplicate image
        os.system("cp TestImages/" + image + " TestImagesDuplicates/" + image)

        #Draw bounding boxes on duplicate image
        img = Image.open("TestImagesDuplicates/" + image)
        print("Drawing bounding boxes on " + image)
        width, height = img.size
        draw = ImageDraw.Draw(img)

        for obj in objects:
            vertex1 = obj.bounding_poly.normalized_vertices[0]
            vertex1.x = int(vertex1.x * width)
            vertex1.y = int(vertex1.y * height)
            vertex2 = obj.bounding_poly.normalized_vertices[1]
            vertex2.x = int(vertex2.x * width)
            vertex2.y = int(vertex2.y * height)
            vertex3 = obj.bounding_poly.normalized_vertices[2]
            vertex3.x = int(vertex3.x * width)
            vertex3.y = int(vertex3.y * height)
            vertex4 = obj.bounding_poly.normalized_vertices[3]
            vertex4.x = int(vertex4.x * width)
            vertex4.y = int(vertex4.y * height)


            draw.line(((int(vertex1.x), int(vertex1.y)), (int(vertex2.x), int(vertex2.y))), fill="red", width=5)
            draw.line(((int(vertex2.x), int(vertex2.y)), (int(vertex3.x), int(vertex3.y))), fill="red", width=5)
            draw.line(((int(vertex3.x), int(vertex3.y)), (int(vertex4.x), int(vertex4.y))), fill="red", width=5)
            draw.line(((int(vertex4.x), int(vertex4.y)), (int(vertex1.x), int(vertex1.y))), fill="red", width=5)



                




if __name__ == "__main__":
    main()
