<?php
require_once('connection.php');

if($_SERVER['REQUEST_METHOD']=='GET') {
    $sqlTbPresences = "SELECT * FROM tb_presences WHERE date = '$currentDate'";
    $sqlTbSalesReports = "SELECT * FROM tb_sales_reports WHERE date = '$currentDate'";
    $sqlTbUsers = "SELECT * FROM tb_users";

    $resPresences = mysqli_query($conn,$sqlTbPresences);
    $resReports = mysqli_query($conn,$sqlTbSalesReports);
    $resUsers = mysqli_query($conn,$sqlTbUsers);
    
    $rowsPresences = mysqli_num_rows($resPresences);
    $rowsReports = mysqli_num_rows($resReports);
    $rowsUsers = mysqli_num_rows($resUsers);

    echo json_encode(array(
        "value"=>1,
        "message" => "Sukses",
        "total_presence"=>$rowsPresences,
        "total_report"=>$rowsReports,
        "total_user"=>$rowsUsers,
        "time"=>date ("H:i")
    ));
    mysqli_close($conn);

} else {

    echo json_encode(array(
        "value"=>0,
        "message" => "Terjadi Kesalahan"
    ));
}
?>