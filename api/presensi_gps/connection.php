<?php
    // header("Content-Type: application/json");

    define('HOST','localhost');
    define('USER','root');
    define('PASS','');
    define('DB','db_presence_gps');
    $conn = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect');

    //Set Time
    date_default_timezone_set('Asia/Makassar');
    $dateMethod = mktime(date('m'), date("d"), date('Y'));
    $currentDate = date('Y-m-d', $dateMethod );
    $currentTime = date ("H-i");
    $mydate = strtotime($currentDate);
    
    //link img
    $linkImg = "http://192.168.1.24/presensi_gps/";

    setlocale(LC_ALL, 'id-ID', 'id_ID');
    //strftime("%A, %d %B %Y", $mydate);
 ?>