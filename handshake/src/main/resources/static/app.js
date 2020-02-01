!function () {
    var port = window.location.port.toString() || 8080
    var host = 'http://localhost:' + port
    !function () {
        function myAjax(url, success, error) {
            $.ajax({
                type: "get",
                dataType: "text",
                timeout: 500,
                contentType: "application/json;charset=utf-8",
                url: host + '/' + url,
                success: success,
                error: function (e) {
                    toastr.error('The request to refresh the data failed,'
                        + ' and the server may be down. code = '
                        + e.status, 'Error');
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
                        text: cacheQrCodeContent,
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
            // requestTemplate();
            // requestDataSource();
        }
        window.setInterval(requestGroup, 2000)
        requestGroup()
    }()
    !function () {
        var btn = document.getElementById('change-focus')
        var input = document.getElementById("focus-input")
        function focus() {
            toastr.info('Submitting.', 'Information');
            var value = input.value
            $.ajax({
                type: "post",
                dataType: "text",
                timeout: 500,
                contentType: "application/json;charset=utf-8",
                url: host + '/focus',
                data: (value || ''),
                success: function () {
                    toastr.success('Aready submitt.', "Success");
                },
                error: function (e) {
                    toastr.error('The request to refresh the data failed,'
                        + ' and the server may be down. code = '
                        + e.status, 'Error');
                }
            });
        }
        btn.onclick = focus;
        $.ajax({
            type: "get",
            dataType: "text",
            timeout: 500,
            contentType: "application/json;charset=utf-8",
            url: host + '/focus',
            success: function (data) {                              
                input.value = data.toString();
            },
            error: function (e) {
                toastr.error('The request to refresh the data failed,'
                    + ' and the server may be down. code = '
                    + e.status, 'Error');
            }
        });
    }()
}()