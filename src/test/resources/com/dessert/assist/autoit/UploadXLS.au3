dim $objTitle = $CmdLine[1]
dim $filePath = $CmdLine[2]
WinWait($var1)
WinActivate($objTitle)
ControlSetText($objTitle, "", "[CLASS:Edit; INSTANCE:1]", $filePath)
ControlClick($objTitle, "", "打开(&O)")