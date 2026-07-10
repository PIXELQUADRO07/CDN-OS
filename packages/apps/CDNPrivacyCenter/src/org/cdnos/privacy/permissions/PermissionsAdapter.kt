package org.cdnos.privacy.permissions

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.cdnos.privacy.R

class PermissionsAdapter(
    private var apps: List<PermissionAppModel>
) : RecyclerView.Adapter<PermissionsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.app_icon)
        val name: TextView = view.findViewById(R.id.app_name)
        val pkg: TextView = view.findViewById(R.id.app_package)
        val cameraImg: ImageView = view.findViewById(R.id.img_camera)
        val micImg: ImageView = view.findViewById(R.id.img_mic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_permission_app, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]
        holder.icon.setImageDrawable(app.icon)
        holder.name.text = app.appName
        holder.pkg.text = app.packageName
        
        holder.cameraImg.visibility = if (app.hasCamera) View.VISIBLE else View.GONE
        holder.micImg.visibility = if (app.hasMic) View.VISIBLE else View.GONE
        
        // Colore rosso se l'app ha il permesso
        holder.cameraImg.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
        holder.micImg.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
    }

    override fun getItemCount() = apps.size

    fun updateData(newApps: List<PermissionAppModel>) {
        apps = newApps
        notifyDataSetChanged()
    }
}
