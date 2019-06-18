<?php
$rootFolder = dirname(__FILE__);
//print("{$rootFolder}\r\n");
$resourceList = scandir($rootFolder);
for($i = 0; $i < count($resourceList); $i++)
{
        if(is_dir($resourceList[$i]))
        {
                if(basename($resourceList[$i]) != "\$1" && $resourceList[$i] != "." && $resourceList[$i] != "..")
                {
                        printDirectory($rootFolder, $resourceList[$i]);
                }
        }
}

function printDirectory($root, $folder)
{
        print("/{$folder}/\r\n");
        //print("{$root}/{$folder}/\r\n");
        $childList = scandir("{$root}/{$folder}/");
        for($i = 0; $i < count($childList); $i++)
        {
                if(is_dir("{$root}/{$folder}/{$childList[$i]}"))
                {
                        if($childList[$i] != "." && $childList[$i] != "..")
                        {
                                printDirectory($root, $folder."/".$childList[$i]);
                        }
                }
                else
                {
                        print("/{$folder}/{$childList[$i]}\r\n");
                }
        }
}
?>
