dim $objTitle = $CmdLine[1]
dim $filePath = $CmdLine[2]
dim $alertTitle = $CmdLine[3]
WinWait($objTitle)
WinActivate($objTitle)
ControlSetText($objTitle, "", "[CLASS:Edit; INSTANCE:1]", $filePath)
ControlClick($objTitle, "", "保存(&S)")
Sleep(1000)
If WinExists($alertTitle) Then
	WinActivate($alertTitle)
	WinWaitActive($alertTitle)
	ControlClick($alertTitle, "", "是(&Y)")
EndIf	
