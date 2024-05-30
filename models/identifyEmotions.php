<!doctype html>
<html lang="ru">
<head>
  <meta charset="utf-8" />
  <title></title>
  <link rel="stylesheet" href="style.css" />
</head>
<body>
    <?php
    $text = $_POST['data'];
 
    ini_set('display_errors', '1');
    ini_set('display_startup_errors', '1');
    error_reporting(E_ALL);
    
    $username = 'z9613607';
    $password = '20Qqk1l18acx';
    
    $connection = ssh2_connect('z9613607.beget.tech', 22);
    if (!$connection) echo "isn't connected";
    
    ssh2_auth_password($connection, $username, $password);
    $stream = ssh2_exec($connection, "cd ~/practicenn/public_html/python/emotionsModel/;python identifyEmotions.py '$text'");

    stream_set_blocking($stream, true);
    $stream_out = ssh2_fetch_stream($stream, SSH2_STREAM_STDIO);
    echo ":";
    echo stream_get_contents($stream_out);
    echo utf8_encode(stream_get_contents($stream_out));
    ?>
</body>
</html>
