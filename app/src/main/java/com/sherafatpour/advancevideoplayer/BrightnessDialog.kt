package com.sherafatpour.advancevideoplayer

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDialogFragment
import com.sherafatpour.advancevideoplayer.databinding.BrightnessDialogBinding

class BrightnessDialog : AppCompatDialogFragment() {

    lateinit var binding: BrightnessDialogBinding
    lateinit var build:AlertDialog.Builder

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding =  BrightnessDialogBinding.inflate(requireActivity().layoutInflater,null,false)
        build = AlertDialog.Builder(requireActivity())
        build.setView(binding.root)

        val brightness = Settings.System.getInt(requireContext().contentResolver,Settings.System.SCREEN_BRIGHTNESS,0)

        binding.brtNumber.text = "$brightness"
        binding.brtSeekbar.progress = brightness
        binding.brtSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                val context = requireContext().applicationContext
                val canWrite= Settings.System.canWrite(context)
                if (canWrite){

                    val sBrightness = progress * 255/255


                    binding.brtNumber.text = "$sBrightness"

                    Settings.System.putInt(context.contentResolver,Settings.System.SCREEN_BRIGHTNESS_MODE,Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
                    Settings.System.putInt(context.contentResolver,Settings.System.SCREEN_BRIGHTNESS,sBrightness)
                }else{

                    Toast.makeText(requireContext(),"Enable write settings for brightness control",Toast.LENGTH_LONG).show()
                  startActivityForResult( Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {

                        data = Uri.parse("package:" + requireContext().packageName)


                    },0)


                }


            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        binding.close.setOnClickListener {

            dismiss()
        }

        return build.create()


    }


}