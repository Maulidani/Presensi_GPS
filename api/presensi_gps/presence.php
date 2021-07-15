<?php
require_once('connection.php');

if($_SERVER['REQUEST_METHOD']=='POST') {
    // header("Content-Type: application/json");

    $response = array();
    $office = "1"; // lokasi kantor
    $idName = $_POST['name'];
    $img = $_POST['img'];
    $imgName = time(). "_" .$currentDate . "_" . $currentTime;
    $imgPath = "img/presensi$imgName.jpg";

    $sql = "SELECT * FROM tb_presences WHERE name = '$idName' AND date = '$currentDate'";
    $check = mysqli_fetch_array(mysqli_query($conn,$sql));
 
    if(isset($check)){
    
        $response["value"] = 0;
        $response["message"] = "Presensi Anda Sudah Terkirim";
        echo json_encode($response);
    } else {
    
        $sql = "INSERT INTO tb_presences (office,name,img)
        VALUES('$office','$idName','$imgPath')";
        if(mysqli_query($conn,$sql)) {
       
            file_put_contents($imgPath,base64_decode($img));
        
            $status = "1";
            $sqlUpdate = "UPDATE tb_users SET status = '$status' WHERE id = '$idName'";
            
            if(mysqli_query($conn,$sqlUpdate)) {
                                
                $response["value"] = 1;
                $response["message"] = "Sukses";
                echo json_encode($response);
                } else {
                    
                $response["value"] = 0;
                $response["message"] = "Gagal";
                echo json_encode($response);
                }

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