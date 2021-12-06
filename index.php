<?php

/* if (isset($_FILES["image"])) {

  $fileName = $_FILES["image"]["name"];
  $fileTmpName = $_FILES["image"]["tmp_name"];
  $fileSize = $_FILES["image"]["size"];
  $fileError = $_FILES["image"]["error"];
  $fileType = $_FILES["image"]["type"];

  $fileExt = explode(".", $fileName);
  $fileActualExt = strtolower(end($fileExt));

  $allowed = array("jpg", "jpeg", "png");

  if (in_array($fileActualExt, $allowed)) {
    if ($fileError === 0) {
      if ($fileSize < 1000000) {
        $fileNameNew = uniqid("", true).".".$fileActualExt;
        $fileDestination = "uploads/".$fileNameNew;

        echo $fileName;

        if (move_uploaded_file($fileTmpName, $fileDestination)) {
          echo "File uploaded successfully";
        } else {
          echo "Error uploading file";
        }
      } else {
        echo "Your file is too big!";
      }
    } else {
      echo "There was an error uploading your file!";
    }
  } else {
    echo "You cannot upload files of this type!";
  }

}
 */

if (isset($_FILES["image"])) {
  
  $target_dir = "uploads/";
  $target_file = $target_dir . basename($_FILES["image"]["name"]);
  $uploadOk = 1;
  $imageFileType = strtolower(pathinfo($target_file,PATHINFO_EXTENSION));

  $check = getimagesize($_FILES["image"]["tmp_name"]);
  if($check !== false) {
    echo "File is an image - " . $check["mime"] . ".";
    $uploadOk = 1;
  } else {
    echo "File is not an image.";
    $uploadOk = 0;
  }

  if ($uploadOk == 0) {
    echo "Sorry, your file was not uploaded.";
  // if everything is ok, try to upload file
  } else {
  if (move_uploaded_file($_FILES["image"]["tmp_name"], $target_file)) {
    echo "The file ". htmlspecialchars( basename( $_FILES["image"]["name"])). " has been uploaded.";
  } else {
    echo "Sorry, there was an error uploading your file.";
  }
}

}

 
?>