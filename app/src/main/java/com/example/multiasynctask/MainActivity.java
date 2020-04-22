package com.example.multiasynctask;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getName();
    private Button btnDefecto, btnSecuencial, btnConcurrente;
    private ProgressBar progressBar1, progressBar2, progressBar3, progressBar4, progressBar5;
    private MyAsyncTask asyncTask1, asyncTask2, asyncTask3, asyncTask4, asyncTask5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar1 = findViewById(R.id.progressbar1);
        progressBar2 = findViewById(R.id.progressbar2);
        progressBar3 = findViewById(R.id.progressbar3);
        progressBar4 = findViewById(R.id.progressbar4);
        progressBar5 = findViewById(R.id.progressbar5);

        btnDefecto = findViewById(R.id.btnDefecto);
        btnDefecto.setOnClickListener(this);
        btnSecuencial = findViewById(R.id.btnSecuencial);
        btnSecuencial.setOnClickListener(this);
        btnConcurrente = findViewById(R.id.btnConcurrente);
        btnConcurrente.setOnClickListener(this);

        //FUNCIONES ADICIONALES PARA INFORMAR DEL NÚMERO DE CORES
        // determinamos el numero de núcleos
        int cores=getNumOfCores();
        TextView txtNumCores=findViewById(R.id.txtNumCores);
        txtNumCores.setText(cores+"");
        TextView txtNumHilosConcurrentes=findViewById(R.id.txtNumThreads);
        txtNumHilosConcurrentes.setText((cores+1)+"");
        //Log.d(TAG, "El número de cores es: "+cores);
    }

    @Override
    public void onClick(View view) {
        //sólo actuamos si las posibles tareas previas se han acabado
        if(asyncTask1==null || (asyncTask5!=null && asyncTask5.getStatus()== AsyncTask.Status.FINISHED)) {
            //resetear las barras de progreso
            progressBar1.setProgress(0);
            progressBar2.setProgress(0);
            progressBar3.setProgress(0);
            progressBar4.setProgress(0);
            progressBar5.setProgress(0);
            //se pulse el que se pulse, creamos las instancias de la tarea asíncrona
            asyncTask1 = new MyAsyncTask(progressBar1);
            asyncTask2 = new MyAsyncTask(progressBar2);
            asyncTask3 = new MyAsyncTask(progressBar3);
            asyncTask4 = new MyAsyncTask(progressBar4);
            asyncTask5 = new MyAsyncTask(progressBar5);

            switch (view.getId()) {
                case R.id.btnDefecto:
                    asyncTask1.execute();
                    asyncTask2.execute();
                    asyncTask3.execute();
                    asyncTask4.execute();
                    asyncTask5.execute();
                    break;

                case R.id.btnSecuencial:
                    asyncTask1.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                    asyncTask2.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                    asyncTask3.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                    asyncTask4.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                    asyncTask5.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                    break;

                default:
                    asyncTask1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    asyncTask2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    asyncTask3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    asyncTask4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    asyncTask5.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    break;
            }
        }

    }

    public class MyAsyncTask extends AsyncTask<Void, Integer, Void> {

        ProgressBar myProgressBar;

        public MyAsyncTask(ProgressBar target) {
            myProgressBar = target;
        }


        @Override
        protected Void doInBackground(Void... params) {
            for(int i=0; i<=100; i+=10){
                publishProgress(i);
                SystemClock.sleep(100);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            myProgressBar.setProgress(values[0]);
        }
    }

    //pruebas => determina el número de núcleos
    private int getNumOfCores()
    {
        try
        {
            int i = new File("/sys/devices/system/cpu/").listFiles(new FileFilter()
            {
                public boolean accept(File params)
                {
                    return Pattern.matches("cpu[0-9]", params.getName());
                }
            }).length;
            return i;
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error determinando el número de procesadores");
        }
        return 1;//en caso de error permite la ejecución secuencial
    }
}
