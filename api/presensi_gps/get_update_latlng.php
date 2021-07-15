<?php
require_once('connection.php');

if($_SERVER['REQUEST_METHOD']=='GET') {
    
    $idName = $_GET['name'];

    $sql = "SELECT * FROM tb_sales_reports WHERE name = '$idName'";
    $res = mysqli_query($conn,$sql);
    $result = array();

    while($row = mysqli_fetch_array($res)){
      array_push($result, array(
        'location_name'=>$row[2],
        'latitude'=>$row[3],
        'longitude'=>$row[4],
        'status'=>$row[9]
        ));
    }
    echo json_encode(array("value"=>1,"result"=>$result));
    mysqli_close($conn);
}
?>