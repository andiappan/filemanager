/*global cordova, module*/

module.exports = {
    upload: function (urlStr,QuoteNo,ParaName,FlowType,successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Filemanager", "upload", [urlStr,QuoteNo,ParaName,FlowType]);
    },
	savesignature: function (baseImg,custId,successCallback, errorCallback) {
		var arr = baseImg.split("data:image/png;base64,");
		var basestr = baseImg;
		if(arr.length != 1){
			basestr = arr[1];
		}
        cordova.exec(successCallback, errorCallback, "Filemanager", "savesignature", [basestr,custId]);
    },
	uploadsignature: function (urlStr,custId,QuoteNo,successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Filemanager", "uploadsignature", [urlStr,custId,QuoteNo]);
    },
	uploadscanner: function (urlStr,QuoteNo,ParaName,FlowType,FilePath,successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Filemanager", "uploadscanner", [urlStr,QuoteNo,ParaName,FlowType,FilePath]);
    }
};