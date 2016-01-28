dim $objTitle = $CmdLine[1]
dim $picPath = $CmdLine[2]
WinWait($objTitle)
WinActive($objTitle)
ControlSetText($objTitle, "", "[CLASS:Edit; INSTANCE:1]", $picPath)
ControlClick($objTitle, "", "´ò¿ª(&O)")
