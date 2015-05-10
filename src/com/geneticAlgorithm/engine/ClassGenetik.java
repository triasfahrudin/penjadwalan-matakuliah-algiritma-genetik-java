/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.geneticAlgorithm.engine;

import com.geneticAlgorithm.database.MysqlConnect;
import com.geneticAlgorithm.util.util;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author theultimate7
 */
public class ClassGenetik {

//    private static final String PRAKTIKUM = "PRAKTIKUM";
//    private static final String TEORI = "TEORI";
//    private static final String LABORATORIUM = "LABORATORIUM";

    private final int jenis_semester;
    private final String tahun_akademik;
    private final int populasi;
    private final float crossOver;
    private final float mutasi;

    private int[] mata_kuliah;
    private int[][][] individu;
    private int[] sks; //sks terikat pada tabel pengampu
    private int[] dosen; //dosen terikat pada tabel pengampu

    private int[] jam;
    private int[] hari;
    private int[] iDosen;

    //waktu keinginan dosen
    private String[][] waktu_dosen;

    private String[] jenis_mk; //reguler or praktikum

    private int[] ruangLaboratorium;
    private int[] ruangReguler;

    private String logAmbilData;
    private String logInisialisasi;

    private String log;
    private int[] induk;

    //jumat
    private final int _kodeJumat;
    private final int[] _rangeJumat;

    private final int _kodeDhuhur;

    public ClassGenetik(int jenis_semester, String tahun_akademik, int populasi,
            float crossOver, float mutasi, int kodeJumat, int[] rangeJumat,
            int kodeDhuhur) {
        this.jenis_semester = jenis_semester;
        this.tahun_akademik = tahun_akademik;
        this.populasi = populasi;
        this.crossOver = crossOver;
        this.mutasi = mutasi;
        this._kodeJumat = kodeJumat;
        this._rangeJumat = rangeJumat;
        this._kodeDhuhur = kodeDhuhur;
    }

    
    
    public final Boolean AmbilData() throws SQLException {

        int jumlah_pengampu = util.getCount( "SELECT a.kode,"
                        + "       b.sks,"
                        + "       a.kode_dosen,"
                        + "       b.jenis "
                        + "FROM pengampu a "
                        + "LEFT JOIN mata_kuliah b "
                        + "ON a.kode_mk = b.kode "
                        + "WHERE b.semester%2 = " + Integer.toString(jenis_semester) + "  AND a.tahun_akademik = '" + tahun_akademik +  "'", "kode");
        
        if(jumlah_pengampu == 0){            
            return FALSE;
        }
        
        //Fill  Array of mata kuliah and SKS Variables
        ResultSet dtMK_Pengampu = MysqlConnect.getDbCon().ExecuteQuery(                
                        "SELECT a.kode,"
                        + "       b.sks,"
                        + "       a.kode_dosen,"
                        + "       b.jenis "
                        + "FROM pengampu a "
                        + "LEFT JOIN mata_kuliah b "
                        + "ON a.kode_mk = b.kode "
                        + "WHERE b.semester%2 = " + Integer.toString(jenis_semester) + "  AND a.tahun_akademik = '" + tahun_akademik +  "'");
                
        
        int i = 0;
        mata_kuliah = new int[jumlah_pengampu];
        sks = new int[jumlah_pengampu];
        dosen = new int[jumlah_pengampu];
        jenis_mk = new String[jumlah_pengampu];
        
        while (dtMK_Pengampu.next()) {
            String id = dtMK_Pengampu.getString("kode");
            int _kode = dtMK_Pengampu.getInt("kode");
            int _sks = dtMK_Pengampu.getInt("sks");
            int _kode_dosen = dtMK_Pengampu.getInt("kode_dosen");
            String _jenis_mk = dtMK_Pengampu.getString("jenis");

            mata_kuliah[i] = _kode;
            sks[i] = _sks;
            dosen[i] = _kode_dosen;
            jenis_mk[i] = _jenis_mk;
            
            i++;
        }


        //Fill Array of Jam Variables 
        int jumlah_jam = util.getCount("SELECT * FROM jam", "kode");
        ResultSet dtJam = MysqlConnect.getDbCon().ExecuteQuery(
                "SELECT kode "
                + "FROM jam");

        int j = 0;
        jam = new int[jumlah_jam];
        while (dtJam.next()) {
            jam[j] = dtJam.getInt("kode");
            j++;
        }

        //Fill Array of Hari Variables 
        int jumlah_hari = util.getCount("SELECT * FROM hari WHERE aktif = 'True'","kode");
        ResultSet dtHari = MysqlConnect.getDbCon().ExecuteQuery(
                "SELECT kode "
                + "FROM hari "
                + "WHERE aktif = 'True'");
        int k = 0;
        hari = new int[jumlah_hari];
        while (dtHari.next()) {
            hari[k] = dtHari.getInt("kode");
            k++;
        }

        //ruang reguler
        int jumlah_ruangReguler = util.getCount("SELECT * FROM ruang WHERE jenis = 'TEORI'", "kode");
        ResultSet dtRuangReguler = MysqlConnect.getDbCon().ExecuteQuery(                
                        "SELECT kode "
                        + "FROM ruang "
                        + "WHERE jenis = 'TEORI'");
        int l = 0;
        ruangReguler = new int[jumlah_ruangReguler];
        while (dtRuangReguler.next()) {
            ruangReguler[l] = dtRuangReguler.getInt("kode");
            l++;
        }


        //ruang reguler
        int jumlah_ruanglaboratorium = util.getCount("SELECT * FROM ruang WHERE jenis = 'LABORATORIUM'","kode");
        ResultSet dtRuangLaboratorium = MysqlConnect.getDbCon().ExecuteQuery(                
                        "SELECT kode "
                        + "FROM ruang "
                        + "WHERE jenis = 'LABORATORIUM'");
        int m = 0;
        ruangLaboratorium = new int[jumlah_ruanglaboratorium];
        while (dtRuangLaboratorium.next()) {
            ruangLaboratorium[m] = dtRuangLaboratorium.getInt("kode");
            m++;
        }

        int jumlah_waktuDosen = util.getCount("SELECT * FROM waktu_tidak_bersedia","kode_dosen");
        ResultSet dtWaktuDosen = MysqlConnect.getDbCon().ExecuteQuery(
                "SELECT kode_dosen, "
                + "CONCAT_WS(':',kode_hari,kode_jam) as waktu_tidak_bersedia "
                + "FROM waktu_tidak_bersedia");

        int n = 0;
        iDosen = new int[jumlah_waktuDosen];
        waktu_dosen = new String[jumlah_waktuDosen][2];
        while (dtWaktuDosen.next()) {
            iDosen[n] = dtWaktuDosen.getInt("kode_dosen");
            waktu_dosen[n][0] = dtWaktuDosen.getString("kode_dosen");
            waktu_dosen[n][1] = dtWaktuDosen.getString("waktu_tidak_bersedia");
            n++;
        }
        return TRUE;


    }

    public final void Inisialisasi() {
        java.util.Random random = new java.util.Random();
        individu = new int[populasi][mata_kuliah.length][4];

        for (int i = 0; i < populasi; i++) {
            for (int j = 0; j < mata_kuliah.length; j++) {
                //  Perulangan untuk pembangkitan jadwal 
                individu[i][j][0] = j; // Penentuan matakuliah dan kelas

                if (sks[j] == 1) // Penentuan jam secara acak ketika 1 sks
                {
                    individu[i][j][1] = random.nextInt(jam.length);
                }
                if (sks[j] == 2) // Penentuan jam secara acak ketika 2 sks
                {
                    individu[i][j][1] = random.nextInt(jam.length - 1);
                }
                if (sks[j] == 3) // Penentuan jam secara acak ketika 3 sks
                {
                    individu[i][j][1] = random.nextInt(jam.length - 2);
                }
                if (sks[j] == 4) // Penentuan jam secara acak ketika 4 sks
                {
                    individu[i][j][1] = random.nextInt(jam.length - 3);
                }
                //System.Threading.Thread.Sleep(1);
                individu[i][j][2] = random.nextInt(hari.length); // Penentuan hari secara acak
                //TODO: jika kuliah reguler => ruang reguler
                //TODO: jika kuliah praktikum => ruang lab 

                //individu[i, j, 3] = random.Next(ruang.Length); // Penentuan ruang secara acak 
                if ("TEORI".equals(jenis_mk[j])) {
                    individu[i][j][3] = ruangReguler[random.nextInt(ruangReguler.length)];                    
                } else {
                    individu[i][j][3] = ruangLaboratorium[random.nextInt(ruangLaboratorium.length)];                   
                }

            }
        }
    }

    private float CekFitness(int indv) {
        //float[,] penalty = new float[populasi, 6];
        float penalty1 = 0, penalty2 = 0, penalty3 = 0, penalty4 = 0, penalty5 = 0;

        for (int i = 0; i < mata_kuliah.length; i++) {
            for (int j = 0; j < mata_kuliah.length; j++) //1.bentrok ruang dan waktu dan 3.bentrok dosen
            {
                //ketika pemasaran matakuliah sama, maka langsung ke perulangan berikutnya
                if (i == j) {
                    continue;
                }

                //Ketika jam,hari dan ruangnya sama, maka penalty + satu 
                if ((individu[indv][i][1] == individu[indv][j][1]) 
                        && (individu[indv][i][2] == individu[indv][j][2]) 
                        && (individu[indv][i][3] == individu[indv][j][3])) {
                    penalty1 += 1;
                }

                //Ketika sks lebih dari 1, 
                //hari dan ruang sama, dan 
                //jam kedua sama dengan jam pertama matakuliah yang lain, maka penalty + 1
                if (sks[i] >= 2) {
                    if ((individu[indv][i][1] + 1 == individu[indv][j][1]) 
                            && (individu[indv][i][2] == individu[indv][j][2]) 
                            && (individu[indv][i][3] == individu[indv][j][3])) {
                        penalty1 += 1;
                    }
                }

                //Ketika sks lebih dari 2, 
                //hari dan ruang sama dan 
                //jam ketiga sama dengan jam pertama matakuliah yang lain, maka penalty + 1
                if (sks[i] >= 3) {
                    if ((individu[indv][i][1] + 2 == individu[indv][j][1]) 
                            && (individu[indv][i][2] == individu[indv][j][2]) 
                            && (individu[indv][i][3] == individu[indv][j][3])) {
                        penalty1 += 1;
                    }
                }

                //Ketika sks lebih dari 3, 
                //hari dan ruang sama dan 
                //jam keempat sama dengan jam pertama matakuliah yang lain, maka penalty + 1
                if (sks[i] >= 4) {
                    if ((individu[indv][i][1] + 3 == individu[indv][j][1]) 
                            && (individu[indv][i][2] == individu[indv][j][2]) 
                            && (individu[indv][i][3] == individu[indv][j][3])) {
                        penalty1 += 1;
                    }
                }

                //______________________BENTROK DOSEN
                if (individu[indv][i][1] == individu[indv][j][1] 
                        && individu[indv][i][2] == individu[indv][j][2] 
                        && dosen[i] == dosen[j]) //ketika jam sama
                //dan hari sama
                //dan dosennya sama
                {
                    //maka...
                    penalty3 += 1;
                }

                if (sks[i] >= 2) //jika lebih dari 1 SKS
                {
                    if ((individu[indv][i][1] + 1) == (individu[indv][j][1]) 
                            && (individu[indv][i][2]) == (individu[indv][j][2]) 
                            && dosen[i] == dosen[j]) //jam ke-2 == dengan jam ke-1 mk yang lain
                    //dan hari sama
                    //dan dosen sama
                    {
                        //maka...
                        penalty3 += 1;
                    }
                }

                if (sks[i] >= 3) //jika lebih dari 2 SKS
                {
                    if ((individu[indv][i][1] + 2) == (individu[indv][j][1]) 
                            && (individu[indv][i][2]) == (individu[indv][j][2]) 
                            && dosen[i] == dosen[j]) //jam ke-3 == dengan jam ke-1 mk yang lain
                    //dan hari sama
                    //dan dosen sama
                    {
                        //maka...
                        penalty3 += 1;
                    }
                }

                if (sks[i] >= 4) //jika lebih dari 3 SKS
                {
                    if ((individu[indv][i][1] + 3) == (individu[indv][j][1]) 
                            && (individu[indv][i][2]) == (individu[indv][j][2]) 
                            && dosen[i] == dosen[j]) //jam ke-4 == dengan jam ke-1 mk yang lain
                    //dan hari sama
                    //dan dosen sama
                    {
                        //maka...
                        penalty3 += 1;
                    }
                }
            } //end 1.bentrok ruang dan waktu dan 3.bentrok dosen

            if (individu[indv][i][2] + 1 == (_kodeJumat)) //2.bentrok sholat jumat
            {
                //int x = individu[indv, i, 2];
                if (sks[i] == (1)) {
                    if (individu[indv][i][1] == (_rangeJumat[0] - 1) 
                            || individu[indv][i][1] == (_rangeJumat[1] - 1) 
                            || individu[indv][i][1] == (_rangeJumat[2] - 1)) {
                        penalty2 += 1;
                    }
                }

                if (sks[i] == (2)) {
                    if (individu[indv][i][1] == (_rangeJumat[0] - 2) 
                            || individu[indv][i][1] == (_rangeJumat[0] - 1) 
                            || individu[indv][i][1] == (_rangeJumat[1] - 1) 
                            || individu[indv][i][1] == (_rangeJumat[2] - 1)) {
                        penalty2 += 1;
                    }
                }

                if (sks[i] == (3)) {
                    if (individu[indv][i][1] == (_rangeJumat[0] - 3) 
                            || individu[indv][i][1] == (_rangeJumat[0] - 2) 
                            || individu[indv][i][1] == (_rangeJumat[0] - 1) 
                            || individu[indv][i][1] == (_rangeJumat[1] - 1) 
                            || individu[indv][i][1] == (_rangeJumat[2] - 1)) {
                        penalty2 += 1;
                    }
                }

                if (sks[i] == (4)) {
                    if (individu[indv][i][1] == (_rangeJumat[0] - 4) 
                            || individu[indv][i][1] == (_rangeJumat[0] - 3) 
                            || individu[indv][i][1] == (_rangeJumat[0] - 2) 
                            || individu[indv][i][1] == (_rangeJumat[0] - 1) 
                            || individu[indv][i][1] == (_rangeJumat[1] - 1) 
                            || individu[indv][i][1] == (_rangeJumat[2] - 1)) {
                        penalty2 += 1;
                    }
                }
            }

            //Boolean penaltyForKeinginanDosen = false;
            for (int j = 0; j < iDosen.length; j++) {
                if (dosen[i] == iDosen[j]) {
                    String[] hari_jam = waktu_dosen[j][1].split("[:]", -1);

                    if ((new Integer(jam[individu[indv][i][1]])).equals(new Integer(hari_jam[1]))
                            && (new Integer(hari[individu[indv][i][2]])).equals(new Integer(hari_jam[0]))) {
                        //penaltyForKeinginanDosen = true;
                        penalty4 += 1;

                    }
                }
            }

            if (individu[indv][i][1] == (_kodeDhuhur - 1)) {
                penalty5 += 1;
            }

        }

        float fitness = 1 / (1 + (penalty1 + penalty2 + penalty3 + penalty4 + penalty5));

        return fitness;
    }

    public final float[] HitungFitness() {
		//hard constraint
        //1.bentrok ruang dan waktu
        //2.bentrok sholat jumat
        //3.bentrok dosen
        //4.bentrok keinginan waktu dosen 
        //5.bentrok waktu dhuhur 
        //=>6.praktikum harus pada ruang lab {telah ditetapkan dari awal perandoman
        //    bahwa jika praktikum harus ada pada LAB dan mata kuliah reguler harus 
        //    pada kelas reguler

        //soft constraint //TODO


        float[] fitness = new float[populasi];

        for (int indv = 0; indv < populasi; indv++) {
            //Cek Fitness
            fitness[indv] = CekFitness(indv);
        }

//        //~~~~~buble sort~~~~~~
//        String[] sort = new String[populasi];
//        //fill the data
//        //
//
//        for (int i = 0; i < populasi; i++) {
//            sort[i] = String.format("\nIndividu %1$s :Fitness %2$s", (i + 1), fitness[i]);
//        }
//
//        try {
//            boolean swapped = true;
//            while (swapped) {
//                swapped = false;
//                for (int i = 0; i < populasi - 1; i++) {
//                    String[] strI = sort[i].split("[.]", -1);
//                    float fitI = Float.parseFloat(String.format("0.%1$s", strI[1]));
//
//                    String[] strJ = sort[i + 1].split("[.]", -1);
//                    float fitJ = Float.parseFloat(String.format("0.%1$s", strJ[1]));
//
//                    if (fitI < fitJ) {
//                        String sTmp = sort[i];
//                        sort[i] = sort[i + 1];
//                        sort[i + 1] = sTmp;
//                        swapped = true;
//                    }
//                }
//            }
//        } catch (NumberFormatException e) {
//            JOptionPane.showConfirmDialog(null, "Kemungkinan data tidak ada untuk Tahun Akademik dan Semester yang terpilih!", "ERROR", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
//            throw e;
//        }

        return fitness;
    }

    public final void Seleksi(float[] fitness) {
        int jumlah = 0;
        int[] rank = new int[populasi];
        induk = new int[populasi];

        for (int i = 0; i < populasi; i++) { //proses ranking berdasarkan nilai fitness
            rank[i] = 1;
            for (int j = 0; j < populasi; j++) { //ketika nilai fitness jadwal sekarang lebih dari nilai fitness jadwal yang lain,
                //ranking + 1;
                //if (i == j) continue;

                if (fitness[i] > fitness[j]) {
                    rank[i] += 1;
                }
            }
            jumlah += rank[i];
        }
        java.util.Random random = new java.util.Random();

        for (int i = 0; i < induk.length; i++) {
            //proses seleksi berdasarkan ranking yang telah dibuat
            //int nexRandom = random.Next(1, jumlah);
            //random = new Random(nexRandom);

            int target = random.nextInt(jumlah);
            int cek = 0;
            for (int j = 0; j < rank.length; j++) {
                cek += rank[j];
                if (cek >= target) {
                    induk[i] = j;
                    break;
                }
            }
        }
    }

    public final void StartCrossOver() {
        int[][][] individu_baru = new int[populasi][mata_kuliah.length][4];

        java.util.Random random = new java.util.Random();

        for (int i = 0; i < populasi; i += 2) //perulangan untuk jadwal yang terpilih
        {
            int b = 0;

            int nexRandom = random.nextInt(9999) + 1;
            random = new java.util.Random(nexRandom);
            double cr = random.nextDouble();

            if (cr < crossOver) {
                //ketika nilai random kurang dari nilai probabilitas pertukaran
                //maka jadwal mengalami prtukaran

                int a = random.nextInt(mata_kuliah.length - 1);
                while (b <= a) {
                    b = random.nextInt(mata_kuliah.length);
                }

                //penentuan jadwal baru dari awal sampai titik pertama
                for (int j = 0; j < a; j++) {
                    for (int k = 0; k < 4; k++) {
                        individu_baru[i][j][k] = individu[induk[i]][j][k];
                        individu_baru[i + 1][j][k] = individu[induk[i + 1]][j][k];
                    }
                }

                //Penentuan jadwal baru dai titik pertama sampai titik kedua
                for (int j = a; j < b; j++) {
                    for (int k = 0; k < 4; k++) {
                        individu_baru[i][j][k] = individu[induk[i + 1]][j][k];
                        individu_baru[i + 1][j][k] = individu[induk[i]][j][k];
                    }
                }

                //penentuan jadwal baru dari titik kedua sampai akhir
                for (int j = b; j < mata_kuliah.length; j++) {
                    for (int k = 0; k < 4; k++) {
                        individu_baru[i][j][k] = individu[induk[i]][j][k];
                        individu_baru[i + 1][j][k] = individu[induk[i + 1]][j][k];
                    }
                }
            } else { //Ketika nilai random lebih dari nilai probabilitas pertukaran, maka jadwal baru sama dengan jadwal terpilih
                for (int j = 0; j < mata_kuliah.length; j++) {
                    for (int k = 0; k < 4; k++) {
                        individu_baru[i][j][k] = individu[induk[i]][j][k];
                        individu_baru[i + 1][j][k] = individu[induk[i + 1]][j][k];
                    }
                }
            }
        }

        //tampilkan individu baru
        individu = new int[populasi][mata_kuliah.length][4];
        System.arraycopy(individu_baru, 0, individu, 0, individu_baru.length);
    }

    public final float[] Mutasi() {
        float[] fitness = new float[populasi];

        java.util.Random random = new java.util.Random();
        //proses perandoman atau penggantian komponen untuk tiap jadwal baru
        for (int i = 0; i < populasi; i++) {

            int nexRandom = random.nextInt(9999) + 1;
            random = new java.util.Random(nexRandom);
            double r = random.nextDouble();

            //Ketika nilai random kurang dari nilai probalitas Mutasi, 
            //maka terjadi penggantian komponen
            if (r < mutasi) {
                //Penentuan pada matakuliah dan kelas yang mana yang akan dirandomkan atau diganti
                int krom = random.nextInt(mata_kuliah.length);

                switch (sks[krom]) {
                    case 1:
                        individu[i][krom][1] = random.nextInt(jam.length);
                        break;
                    case 2:
                        individu[i][krom][1] = random.nextInt(jam.length - 1);
                        break;
                    case 3:
                        individu[i][krom][1] = random.nextInt(jam.length - 2);
                        break;
                    case 4:
                        individu[i][krom][1] = random.nextInt(jam.length - 3);
                        break;
                }
                //Proses penggantian hari
                individu[i][krom][2] = random.nextInt(hari.length);

                //proses penggantian ruang
                //individu[i, krom, 3] = random.Next(ruang.Length);
                if ("TEORI".equals(jenis_mk[krom])) {
                    individu[i][krom][3] = ruangReguler[random.nextInt(ruangReguler.length)];
                } else {
                    individu[i][krom][3] = ruangLaboratorium[random.nextInt(ruangLaboratorium.length)];
                }

            }
            fitness[i] = CekFitness(i);

        }
        return fitness;
    }

    public final int[][] GetIndividu(int indv) {
        //return individu;

        int[][] individu_solusi = new int[mata_kuliah.length][4];

        for (int j = 0; j < mata_kuliah.length; j++) {
            individu_solusi[j][0] = mata_kuliah[individu[indv][j][0]];
            individu_solusi[j][1] = jam[individu[indv][j][1]];
            individu_solusi[j][2] = hari[individu[indv][j][2]];
            individu_solusi[j][3] = individu[indv][j][3];
        }

        return individu_solusi;
    }

}
