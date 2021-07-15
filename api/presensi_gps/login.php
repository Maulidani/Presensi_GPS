<?php
require_once('connection.php');

if($_SERVER['REQUEST_METHOD']=='POST') {

    $response = array();
    $email = $_POST['email'];
    $password = $_POST['password'];
    
    $sql = "SELECT tb_users.id, tb_users.name, tb_positions.position, tb_users.email, tb_users.password, tb_users.img 
    FROM tb_users, tb_positions 
    WHERE tb_users.position = tb_positions.id
    AND email = '$email' 
    AND password = '$password'";

    $check = mysqli_fetch_array(mysqli_query($conn,$sql));
    if(isset($check)){
        $response["value"] = 1;
        $response["message"] = "Sukses";
        $response["id"] = $check[0];
        $response["name"] = $check[1];
        $response["position"] = $check[2];
        $response["email"] = $check[3];
        $response["password"] = $check[4];
        $response["img"] = $linkImg.$check[5];
        echo json_encode($response);
    } else {
        $response["value"] = 0;
        $response["message"] = "Gagal";
        echo json_encode($response);
    } 
    mysqli_close($conn);
} else {
    $response["value"] = 0;
    $response["message"] = "Terjadi kesalahan";
    echo json_encode($response);
}
?>