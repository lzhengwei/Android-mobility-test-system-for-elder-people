# Android-mobility-test-system-for-elder-people

Android 老人行動力檢測系統
![系統架構圖](https://github.com/lzhengwei/Android-mobility-test-system-for-elder-people/blob/master/System%20Er-diagram.jpg))

在台灣處於高齡化社會的情況下，老年人的健康及照護變得更為要。
為了能夠提早偵測老年人的行動力表現異常，我們利用最常見手機APP來實作三種檢測身體行動力的量表，量表名稱分別為TUG、IADL、BADL
使得一般民眾也能輕易的檢測出自己的身體狀況，並可作為輔助醫生診斷的簡便工具之一。
本專題透過手機的感測器三軸加速規、陀螺儀、磁場感應器來將量表的測試功能實作與應用
也可結合Msp430腳部穿戴式加速規感測器裝置於雙腳，取得更精準的雙腳加速規訊號
藉由手機及腳部穿戴式加速規分析獲得的生理動態資料，來判斷各個量表中動作的完成度獲時間，評斷出一個準確的分數
並且同步儲存診斷的資料於資料庫中，建構一個行動力的檢測系統。

![APP介面簡介](https://github.com/lzhengwei/Android-mobility-test-system-for-elder-people/blob/master/%E6%B5%81%E7%A8%8B%E5%9C%96.jpg))

![MSP430加速規感測器](https://github.com/lzhengwei/Android-mobility-test-system-for-elder-people/blob/master/IMAG1080.jpg))

