;VStar InnoSetup Script

#define TheGroupName "VStar"
#define TheAppName "AAVSO VStar"
#define TheAppDebugName "VStar Debug Mode"
; Normally, TheAppVersion defined via ISCC.exe command-line parameter (see build-win-installer.xml)
#ifndef TheAppVersion
  #define TheAppVersion "Unversioned"
#endif
#define TheAppPublisher "AAVSO"
#define TheAppURL "https://aavso.org/vstar"
#define TheAppExeName "VStar.exe"
#define TheAppCnfName "VStar.ini"
#define TheAppConfig "VStar Launcher Configuration"

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{1860E797-A226-48ED-891A-2B3A0960A83D}
AppName={#TheAppName}
AppVersion={#TheAppVersion}
AppPublisher={#TheAppPublisher}
AppPublisherURL={#TheAppURL}
AppSupportURL={#TheAppURL}
AppUpdatesURL={#TheAppURL}
DefaultDirName={%HOMEDRIVE}{%HOMEPATH}\vstar
DefaultGroupName={#TheGroupName}
;DisableProgramGroupPage=yes
DisableWelcomePage=no
WizardImageFile=tenstar_artist_conception1.bmp
WizardSmallImageFile=aavso.bmp
OutputBaseFilename=VStarSetup_{#TheAppVersion}
OutputDir=..\
Compression=lzma
SolidCompression=yes
PrivilegesRequired=lowest

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags:

[Files]
; NOTE: Don't use "Flags: ignoreversion" on any shared system files
Source: "vstar\{#TheAppExeName}"; DestDir: "{app}"            ; Flags: ignoreversion
Source: "vstar\{#TheAppCnfName}"; DestDir: "{app}"            ; Flags: ignoreversion
// We should copy VStar.bat to destination (it will be rewritten in 'PostInstall') to make it uninstallable.
Source: "vstar\VStar.bat"       ; DestDir: "{app}"            ; Flags: ignoreversion
Source: "vstar\VeLa.bat"        ; DestDir: "{app}"            ; Flags: ignoreversion
Source: "vstar\ChangeLog.txt"   ; DestDir: "{app}"            ; Flags: ignoreversion
Source: "vstar\data\*"          ; DestDir: "{app}\data"       ; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "vstar\dist\*"          ; DestDir: "{app}\dist"       ; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "vstar\doc\*"           ; DestDir: "{app}\doc"        ; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "vstar\extlib\*"        ; DestDir: "{app}\extlib"     ; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "vstar\plugin-dev\*"    ; DestDir: "{app}\plugin-dev" ; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
;Name: "{userprograms}\{#TheAppName}"; Filename: "{app}\{#TheAppExeName}"
Name: "{group}\{#TheAppName}"; Filename: "{app}\{#TheAppExeName}"
Name: "{group}\{#TheAppDebugName}"; Filename: "{app}\{#TheAppExeName}"; Parameters: "//DEBUG"
Name: "{group}\Restore Default Memory Options"; Filename: "{app}\{#TheAppExeName}"; Parameters: "//RESTORE"
Name: "{group}\{#TheAppConfig}"; Filename: "{app}\{#TheAppCnfName}"
Name: "{userdesktop}\{#TheAppName}" ; Filename: "{app}\{#TheAppExeName}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#TheAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(TheAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent
Filename: "{#TheAppURL}"; Description: "Visit Application Website"; Flags: postinstall shellexec skipifsilent

[INI]
Filename: "{app}\{#TheAppCnfName}"; Section: "Settings"; Key: "Parameters"; String: {code:GetIniMemParameters}
Filename: "{app}\{#TheAppCnfName}"; Section: "Settings"; Key: "Description"; String: {code:GetIniDescription}

[Code]

type
  DWORDLONG = Int64; // Should be unsigned, currently not available
  TMemoryStatusEx = record
    dwLength: DWORD;
    dwMemoryLoad: DWORD;
    ullTotalPhys: DWORDLONG;
    ullAvailPhys: DWORDLONG;
    ullTotalPageFile: DWORDLONG;
    ullAvailPageFile: DWORDLONG;
    ullTotalVirtual: DWORDLONG;
    ullAvailVirtual: DWORDLONG;
    ullAvailExtendedVirtual: DWORDLONG;
  end;

var
  IniMemParameters: string;

function GlobalMemoryStatusEx(var lpBuffer: TMemoryStatusEx): BOOL;
  external 'GlobalMemoryStatusEx@kernel32.dll stdcall';

function MemSize: Int64;
var
  MemoryStatus: TMemoryStatusEx;
begin
  Result := 0;
  MemoryStatus.dwLength := SizeOf(MemoryStatus);
  if GlobalMemoryStatusEx(MemoryStatus) then
    Result := MemoryStatus.ullTotalPhys;
end;

function DateTime: String;
begin
  Result := GetDateTimeString('yyyy/mm/dd"T"hh:nn:ss', '-', ':');
end;

function InitIniMemParameters: string;
var
  MaxHeapSize: Int64;
begin
  MaxHeapSize := ((MemSize div 1024) div 1024) div 2; // half of available physical memory, in megabytes.
  if MaxHeapSize < 256 then
    MaxHeapSize := 256; // default value
  if (not IsWin64) and (MaxHeapSize > 1500) then
    MaxHeapSize := 1500;
  // Max heap size cannot be less than initial heap size
  if MaxHeapSize > 800 then
    Result := '-Xms800m -Xmx' + Int64toStr(MaxHeapSize) + 'm'
  else
    Result := '-Xmx' + Int64toStr(MaxHeapSize) + 'm';
end;

function GetIniMemParameters(Param: string): string;
begin
  Result := IniMemParameters;
end;

function GetIniDescription(Param: string): string;
begin
  Result := 'VStar.exe configuration file created at ' + DateTime;
end;

procedure MakeBatLauncher();
var
  S: String;
begin
  S := 
    '@echo off'#13#10 +
    ''#13#10 +
    ':: An alternative VStar launcher created at ' + DateTime + #13#10 +
    ''#13#10 +
    ':: VSTAR_HOME needs to be set to the VStar root directory,'#13#10 +
    ':: e.g. set VSTAR_HOME=C:\vstar'#13#10 +
    ':: If not set, the script assumes the current directory is the'#13#10 +
    ':: directory that the script is running from.'#13#10 +
    ''#13#10 +
    'title VStar'#13#10 +
    ''#13#10 +
    'if not "%VSTAR_HOME%" == "" goto :RUN'#13#10 +
    ''#13#10 +
    'set VSTAR_HOME=%~dp0'#13#10 +
    ''#13#10 +
    ':RUN'#13#10 +
    'java -splash:"%VSTAR_HOME%\extlib\vstaricon.png" ' + GetIniMemParameters('') + ' -jar "%VSTAR_HOME%\dist\vstar.jar" %*'#13#10 +
    'if ERRORLEVEL 1 goto :ERROR'#13#10 +
    'goto :EOF'#13#10 +
    ''#13#10 +
    ':ERROR'#13#10 +
    'echo *** Nonzero exit code: possible ERROR running VStar'#13#10 +
    'pause'#13#10;
  if not SaveStringsToFile(ExpandConstant('{app}') + '\VStar.bat', [S], False) then begin
    MsgBox('Cannot create VStar.bat', mbError, MB_OK);
  end;
end;

procedure CurStepChanged(CurStep: TSetupStep);
begin
  if CurStep = ssPostInstall then begin
    MakeBatLauncher;
    MsgBox('Java memory options were set to'#13#10 + 
            GetIniMemParameters('') + #13#10 +
           'To modify them, edit ' + #13#10 + 
           '"' + ExpandConstant('{app}') + '\' + '{#TheAppCnfName}"'#13#10 + 
           'file.', 
           mbInformation, MB_OK);
  end;
end;

procedure InitializeWizard;
var
  RichViewer: TRichEditViewer;
  Message: String;
  //JavaVer: string;
  JavaFound: Boolean;
  ResultCode: Integer;
begin
  RichViewer := TRichEditViewer.Create(WizardForm);
  RichViewer.Left := WizardForm.WelcomeLabel2.Left;
  RichViewer.Top := WizardForm.WelcomeLabel2.Top;
  RichViewer.Width := WizardForm.WelcomeLabel2.Width;
  RichViewer.Height := WizardForm.WelcomeLabel2.Height;
  RichViewer.Parent := WizardForm.WelcomeLabel2.Parent;
  RichViewer.BorderStyle := bsNone;
  RichViewer.TabStop := False;
  RichViewer.ReadOnly := True;
  WizardForm.WelcomeLabel2.Visible := False;
  Message := 
    '{\rtf1 This will install {#SetupSetting("AppName")} ' + 
    'version {#SetupSetting("AppVersion")} on your computer.\par\par ' +
    'Click Next to continue or Cancel to exit Setup.';

  // Cannot read HKLM if PrivilegesRequired=lowest.
  //JavaVer := '';
  //if not RegQueryStringValue(HKEY_LOCAL_MACHINE, 'SOFTWARE\JavaSoft\Java Runtime Environment',
  //  'CurrentVersion', JavaVer)
  //then
  //  JavaVer := '';
  
  // Instead checking The Registry, testing for the JRE executable.
  try
    JavaFound := Exec('javaw.exe', '', '', SW_HIDE, ewNoWait, ResultCode);
  except
    JavaFound := False;
  end;
  if not JavaFound then begin
    Message := Message + 
      '\par\par Test for "javaw.exe" failed!\par It seems there is no Java Runtime Environment (JRE) installed on your machine.\par ' + 
      'You can download JRE installer from the Java download site \par' + 
      '{\field{\*\fldinst{HYPERLINK "https://www.java.com/download/"}}{\fldrslt{\ul\cf1 https://www.java.com/download/}}}';
  end;
  Message := Message + '}';
  RichViewer.RTFText := Message;

  IniMemParameters := InitIniMemParameters;
end;

