package com.example.pertemuan3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import com.example.pertemuan3.ui.theme.Pertemuan3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pertemuan3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    KartuMahasiswaBox(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
@Composable
fun KartuMahasiswaBox(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFEFEFEF))
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Judul di atas, center
            Text(
                text = "Kartu Mahasiswa",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Foto profil di tengah
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Foto Mahasiswa",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Data identitas
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.85f),
                horizontalAlignment = Alignment.Start
            ) {
                Row {
                    Text(
                        text = "Nama",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray,
                        modifier = Modifier.width(100.dp)
                    )
                    Text(
                        text = ": Arya Rasyad Alamsyah",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }

                Row {
                    Text(
                        text = "NIM",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray,
                        modifier = Modifier.width(100.dp)
                    )
                    Text(
                        text = ": 23010003",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }

                Row {
                    Text(
                        text = "Prodi",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray,
                        modifier = Modifier.width(100.dp)
                    )
                    Text(
                        text = ": Teknik Informatika S1",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }

                Row {
                    Text(
                        text = "Universitas",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray,
                        modifier = Modifier.width(100.dp)
                    )
                    Text(
                        text = ": STMIK Mardira Indonesia",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun KartuMahasiswaPreview() {
    Pertemuan3Theme {
        KartuMahasiswaBox()
    }
}