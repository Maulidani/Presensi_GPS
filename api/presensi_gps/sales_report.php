<?php
require_once('connection.php');

if($_SERVER['REQUEST_METHOD']=='POST') {
    $response = array();
    $idName = $_POST['name'];
    $locationName = $_POST['location_name'];
    $latitude = $_POST['latitude'];
    $longitude = $_POST['longitude'];
    $img = $_POST['img'];
    $notes = $_POST['notes'];
    $imgName = time(). "_" .$currentDate . "_" . $currentTime;
    $imgPath = "img/laporan$imgName.jpg";

    $sql = "SELECT * FROM tb_sales_reports WHERE name = '$idName' 
    AND date = '$currentDate'
    AND latitude = '$latitude'
    AND longitude = '$longitude'";
    $check = mysqli_fetch_array(mysqli_query($conn,$sql));
  
    if(isset($check)){
        $response["value"] = 0;
        $response["message"] = "Anda sudah melapor beberapa saat yang lalu ditempat yang sama";
        echo json_encode($response);
    } else {
      
        $sql = "INSERT INTO tb_sales_reports
        (name,location_name,latitude,longitude,img,notes)
        VALUES
        ('$idName','$locationName','$latitude','$longitude','$imgPath','$notes')";

        if(mysqli_query($conn,$sql)) {
        
            file_put_contents($imgPath,base64_decode($img));

            $response["value"] = 1;
            $response["message"] = "Sukses";
            echo json_encode($response);
        } else {
          
            $response["value"] = 0;
            $response["message"] = "Gagal";
            echo json_encode($response);
        }
    }
        mysqli_close($conn);
    } else {

        $response["value"] = 0;
        $response["message"] = "Terjadi Kesalahan";
        echo json_encode($response);
}
?>