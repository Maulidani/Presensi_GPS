<?php
require_once('connection.php');

if($_SERVER['REQUEST_METHOD']=='POST') {
    $response = array();
    $name = $_POST['name'];
    $email = $_POST['email'];
    $password = $_POST['password'];
    $position = $_POST['position'];
    $idPosition;
    
    $sql = "SELECT * FROM tb_users WHERE email ='$email'";
    $check = mysqli_fetch_array(mysqli_query($conn,$sql));
    if(isset($check)){
        $response["value"] = 0;
        $response["message"] = "Email sudah terdaftar";
        echo json_encode($response);
    } else {
        $sqlGetIdPosition = "SELECT * FROM tb_positions
        WHERE position = '$position'";

        $checkId = mysqli_fetch_array(mysqli_query($conn,$sqlGetIdPosition));
        if(isset($checkId)){
            $idPosition = $checkId[0];
        }

        $sql = "INSERT INTO tb_users (name,position,email,password)
        VALUES('$name','$idPosition','$email','$password')";

        if(mysqli_query($conn,$sql)) {
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
    $response["message"] = "Terjadi kesalahan";
    echo json_encode($response);
}
?>