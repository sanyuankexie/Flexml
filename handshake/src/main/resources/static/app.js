!function () {
    var port = window.location.port.toString() || 8080
    var host = 'http://localhost:' + port;
    !function () {
        function removeAll(node) {
            node.innerHTML = '';
        }
        function myAjax(url, success, error) {
            $.ajax({
                type: "get",
                dataType: "text",
                timeout: 500,
                contentType: "application/json;charset=utf-8",
                url: host + '/' + url,
                success: success,
                error: error
            });
        }
        var requestQrCode = function () {
            var qrcode = document.getElementById("qrcode");
            var cacheQrCodeContent = "";
            return function () {
                myAjax(
                    'qrcode',
                    function (data) {
                        if (cacheQrCodeContent != data.toString()) {
                            cacheQrCodeContent = data.toString()
                            removeAll(qrcode);
                            new QRCode(qrcode, {
                                text: cacheQrCodeContent,
                                width: 128,
                                height: 128,
                                colorDark: "#000000",
                                colorLight: "#ffffff",
                                correctLevel: QRCode.CorrectLevel.H
                            });
                        }
                    },
                    function (e) {
                        var loading = 'qrcode_loading.jpg';
                        if (qrcode.firstChild.src != loading) {
                            removeAll(qrcode);
                            var image = document.createElement("img");
                            image.src = loading;
                            image.style.width = '128px';
                            image.style.height = '128px';
                            image.style.margin = '0px';
                            image.style.padding = '0px';
                            qrcode.appendChild(image);
                        }
                        toastr.error('Request for new qrcode failed. status code = ' + e.status, 'Error');
                    })
            }
        }();
        var requestTemplate = function () {
            var template = document.getElementById("template");
            return function () {
                var cache = null;
                myAjax(
                    "template",
                    function (code) {                        
                        if (code.toString() != cache) {
                            removeAll(template);
                            var formated = formatJson(code);
                            var html = Prism.highlight(formated, Prism.languages.json, 'json');
                            template.innerHTML = html;
                            cache = code;
                        }
                    },
                    function () {
                        cache = null;
                        removeAll(template);
                        template.innerHTML = "<br/>//now this is empty."
                    }
                );
            }
        }();
        function requestGroup() {
            requestQrCode();
            requestTemplate();
        }
        window.setInterval(requestGroup, 2000)
        requestGroup()
    }();
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
                    input.value = '';
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
    }();
}();