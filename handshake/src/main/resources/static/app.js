!function () {
    var port = window.location.port.toString() || 8080
    function myAjax(url, success, error) {
        $.ajax({
            type: "get",
            timeout: 500,
            contentType: "application/json;charset=utf-8",
            url: 'http://localhost:' + port + '/' + url,
            success: success,
            error: function () {
                toastr.error('The request to refresh the data failed, and the server may be down.', 'Error');
                if (error != null) {
                    error()
                }
            }
        });
    }
    var root = document.getElementById("qrcode");
    var cacheQrCodeContent = "";
    function requestQrCode() {
        myAjax('qrcode', function (data) {
            if (cacheQrCodeContent != data.toString()) {
                cacheQrCodeContent = data.toString()
                while (root.hasChildNodes()) {
                    root.removeChild(root.firstChild);
                }
                new QRCode(root, {
                    text: host,
                    width: 128,
                    height: 128,
                    colorDark: "#000000",
                    colorLight: "#ffffff",
                    correctLevel: QRCode.CorrectLevel.H
                });
            }
        })
    }
    function requestTemplate() {
        myAjax("template", function (data) {

        })
    }
    function requestDataSource() {
        myAjax("datasource", function (data) {

        })
    }
    function requestGroup() {
        requestQrCode();
        requestTemplate();
        requestDataSource();
    }
    window.setInterval(requestGroup, 2000)
    requestGroup()
}()