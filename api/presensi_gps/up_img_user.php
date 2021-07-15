<?php
require_once('connection.php');

if($_SERVER['REQUEST_METHOD']=='POST') {
    $response = array();
    $id = $_POST['id'];
    $img = $_POST['img'];
    $imgPath = "img/profil"."_".time().".jpg";

    $sql = "UPDATE tb_users SET img = '$imgPath'
    WHERE id = '$id'";

    if(mysqli_query($conn,$sql)) {

        file_put_contents($imgPath,base64_decode($img));

        $sql = "SELECT img FROM tb_users WHERE id = '$id'";
    
        $check = mysqli_fetch_array(mysqli_query($conn,$sql));
        if(isset($check)){
            $response["value"] = 1;
            $response["message"] = "Sukses";
            $response["img"] = $linkImg.$check[0];
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
    mysqli_close($conn);

} else {
    $response["value"] = 0;
    $response["message"] = "Terjadi kesalahan";
    echo json_encode($response);
}

?>