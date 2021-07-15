<?php
require_once('connection.php');

if($_SERVER['REQUEST_METHOD']=='POST') {
    $response = array();
    $idName = $_POST['id'];
    $status = "0";

    $sql = "UPDATE tb_users SET status = '$status' WHERE id = '$idName'";
    
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