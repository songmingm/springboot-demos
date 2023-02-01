import SparkMD5 from "spark-md5";
const DEFAULT_SIZE = 20 * 1024 * 1024; //每个分片的大小 20m
const md5 = (file, chunkSize = DEFAULT_SIZE) => {
  return new Promise((resolve, reject) => {
    const startMs = new Date().getTime(); //开始上传的时间
    let blobSlice =
      File.prototype.slice ||
      File.prototype.mozSlice ||
      File.prototype.webkitSlice;
    let chunks = Math.ceil(file.size / chunkSize); //计算分片数量
    let currentChunk = 0;
    let spark = new SparkMD5.ArrayBuffer(); //追加数组缓冲区
    let fileReader = new FileReader(); //读取文件
    fileReader.onload = function (e) {
      spark.append(e.target.result);
      currentChunk++;
      if (currentChunk < chunks) {
        loadNext();
      } else {
        const md5 = spark.end(); //完成md5的计算，返回十六进制结果
        console.log(
          "文件md5计算结束，总耗时：",
          (new Date().getTime() - startMs) / 1000,
          "s"
        );
        resolve(md5);
      }
    };
    fileReader.onerror = function (e) {
      reject(e);
    };

    function loadNext() {
      console.log("当前part number：", currentChunk, "总块数：", chunks);
      let start = currentChunk * chunkSize; //开始分片位置
      let end = start + chunkSize;//结束分片位置
      end > file.size && (end = file.size);
      fileReader.readAsArrayBuffer(blobSlice.call(file, start, end));
    }
    loadNext();
  });
};

export default md5;
