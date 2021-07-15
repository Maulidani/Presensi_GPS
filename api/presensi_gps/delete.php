<?php
require_once('connection.php');

if($_SERVER['REQUEST_METHOD']=='POST') {

  $response = array();
  //mendapatkan data
  $id = $_POST['id'];
  $type = $_POST['type'];

  if ($type==="presence") {

    $sql = "DELETE FROM tb_presences WHERE id = '$id'";
    
    if(mysqli_query($conn,$sql)) {

      $response["value"] = 1;
      $response["message"] = "Sukses";
      echo json_encode($response);

    }
    
  } else if ($type==="report") {

    $sql = "DELETE FROM tb_sales_reports WHERE id = '$id'";
    
    if(mysqli_query($conn,$sql)) {

      $response["value"] = 1;
      $response["message"] = "Sukses";
      echo json_encode($response);

    }

  } else if ($type==="user") {

    $sql = "DELETE FROM tb_users WHERE id = '$id'";

    if(mysqli_query($conn,$sql)) {

      $response["value"] = 1;
      $response["message"] = "Sukses";
      echo json_encode($response);

    } 

  } else {
    $response["value"] = 0;
    $response["message"] = "Terjadi Kesalahan";
    echo json_encode($response);
  }

  mysqli_close($conn);
}  else {
    $response["value"] = 0;
    $response["message"] = "Terjadi kesalahan";
    echo json_encode($response);
}
?>