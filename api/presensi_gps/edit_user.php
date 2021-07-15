<?php
require_once('connection.php');

if($_SERVER['REQUEST_METHOD']=='POST') {
    $response = array();
    $id = $_POST['id'];
    $name = $_POST['name'];
    $email = $_POST['email'];
    $password = $_POST['password'];
    $position = $_POST['position'];
    $idPosition;

    $sqlGetIdPosition = "SELECT * FROM tb_positions
    WHERE position = '$position'";
    
    $checkId = mysqli_fetch_array(mysqli_query($conn,$sqlGetIdPosition));
    if(isset($checkId)){
        $idPosition = $checkId[0];
    }

    $sql = "UPDATE tb_users SET name = '$name',position = '$idPosition',
    email = '$email',password = '$password' 
    WHERE id = '$id'";
    
    if(mysqli_query($conn,$sql)) {
                        
        $response["value"] = 1;
        $response["message"] = "Sukses";
        echo json_encode($response);
        } else {
            
        $response["value"] = 0;
        $response["message"] = "Gagal";
        echo json_encode($response);
        }
    mysqli_close($conn);

} else {

    $response["value"] = 0;
    $response["message"] = "Terjadi Kesalahan";
    echo json_encode($response);
}
?>