http.createServer(function (req, res) {
  res.writeHead(200, { 'Content-Type': 'text/plain' });
  res.write('Chinese Checkers' + '\n');
  res.end();
}).listen(8080);